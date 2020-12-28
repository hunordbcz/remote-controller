package net.debreczeni.remotedesktop.ui;

import net.debreczeni.remotedesktop.adapter.MouseAdapter;
import net.debreczeni.remotedesktop.listeners.ScreenShareEventListener;
import net.debreczeni.remotedesktop.model.socket.RemoteImage;
import net.debreczeni.remotedesktop.model.socket.events.KeyboardEvent;
import net.debreczeni.remotedesktop.model.socket.events.MouseMovementEvent;
import net.debreczeni.remotedesktop.model.socket.events.RemoteEvent;
import net.debreczeni.remotedesktop.model.socket.events.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ScreenShare extends JFrame {
    private final static Dimension minimumSize = new Dimension(1024, 576);

    private final List<ScreenShareEventListener> eventListeners;
    private final String serverName;
    private final Container contentPane;
    private final int screenNr;
    private ScreenView screenView;
    private boolean first = true;

    public ScreenShare(String serverName, int screenNr, boolean allowControl) {
        this.serverName = serverName;
        this.screenNr = screenNr;
        this.eventListeners = new ArrayList<>();
        this.contentPane = getContentPane();

        initUI();
        initComponents();

        if (allowControl) {
            initControlListeners();
        }

        EventQueue.invokeLater(() -> setVisible(true));
    }

    public void initListeners(){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                notifyQuit();
            }
        });
    }

    private void initControlListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                notifyRemoteEvent(new KeyboardEvent(KeyboardEvent.TYPE.PRESS, e.getKeyCode()));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                notifyRemoteEvent(new KeyboardEvent(KeyboardEvent.TYPE.RELEASE, e.getKeyCode()));
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                notifyRemoteEvent(new MouseEvent(MouseEvent.TYPE.PRESS, e.getButton()));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                notifyRemoteEvent(new MouseEvent(MouseEvent.TYPE.RELEASE, e.getButton()));
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                notifyRemoteEvent(new MouseMovementEvent(e.getPoint(), screenNr));
            }
        });
    }

    private void notifyRemoteEvent(RemoteEvent remoteEvent) {
        eventListeners.forEach(eventListener -> eventListener.newRemoteEvent(remoteEvent));
    }

    private void notifyQuit() {
        eventListeners.forEach(ScreenShareEventListener::quitButtonPressed);
        dispose();
    }

    private void initUI() {
        setTitle(serverName);
        setMinimumSize(minimumSize);
        setFocusable(true);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JButton button = new JButton("Quit");
        button.addActionListener(action -> notifyQuit());
        contentPane.add(button, BorderLayout.PAGE_START);

        screenView = new ScreenView();
        contentPane.add(screenView, BorderLayout.CENTER);

        MouseAdapter mouseAdapter = new MouseAdapter(screenView);
        screenView.addMouseMotionListener(mouseAdapter);
        screenView.addMouseWheelListener(mouseAdapter);
    }


    public void updateImage(RemoteImage image) {
        screenView.updateImage(image);

        if (first) {
            revalidate();
            pack();
            repaint();

            first = false;
        }
    }

    public void addEventListener(ScreenShareEventListener eventListener) {
        eventListeners.add(eventListener);
    }

}
