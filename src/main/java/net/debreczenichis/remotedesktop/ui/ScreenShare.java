package net.debreczenichis.remotedesktop.ui;

import net.debreczenichis.remotedesktop.listeners.ScreenShareEventListener;
import net.debreczenichis.remotedesktop.model.socket.RemoteImage;
import net.debreczenichis.remotedesktop.model.socket.events.KeyboardEvent;
import net.debreczenichis.remotedesktop.model.socket.events.MouseEvent;
import net.debreczenichis.remotedesktop.model.socket.events.MouseMovementEvent;
import net.debreczenichis.remotedesktop.model.socket.events.RemoteEvent;

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
    private final Dimension nativeSize;
    private ScreenView screenView;
    private boolean first = true;
    private boolean mouseListenerEnabled;
    private boolean keyboardListenerEnabled;

    private JButton quitButton;
    private JButton nativeResolutionButton;
    private JButton keyboardEnableButton;
    private JButton mouseEnableButton;

    public ScreenShare(String serverName, int screenNr, Dimension dimension, boolean allowControl) {
        this.serverName = serverName;
        this.screenNr = screenNr;
        this.eventListeners = new ArrayList<>();
        this.contentPane = getContentPane();
        this.nativeSize = dimension;

        initUI();
        initComponents();
        initListeners();

        if (allowControl) {
            initControlListeners();
            mouseListenerEnabled = true;
            mouseEnableButton.setBackground(Color.GREEN);

            keyboardListenerEnabled = true;
            keyboardEnableButton.setBackground(Color.GREEN);
        } else {
            mouseListenerEnabled = false;
            mouseEnableButton.setEnabled(false);

            keyboardListenerEnabled = false;
            keyboardEnableButton.setEnabled(false);
        }

        EventQueue.invokeLater(() -> setVisible(true));
    }

    public void initListeners() {
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

        screenView.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                notifyRemoteEvent(new MouseEvent(MouseEvent.TYPE.PRESS, e.getButton()));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                notifyRemoteEvent(new MouseEvent(MouseEvent.TYPE.RELEASE, e.getButton()));
            }
        });

        screenView.addMouseMotionListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                Point pointOnImage = screenView.getPointOnImage(e.getPoint());
                if (pointOnImage == null) {
                    return;
                }
                Point pointOnScreen = screenView.getPointOnScreen(pointOnImage, nativeSize);

                notifyRemoteEvent(new MouseMovementEvent(pointOnScreen, screenNr));
            }
        });

        screenView.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                notifyRemoteEvent(new net.debreczenichis.remotedesktop.model.socket.events.MouseWheelEvent(e.getScrollAmount()));
            }
        });
    }

    private void notifyRemoteEvent(RemoteEvent remoteEvent) {
        if (remoteEvent instanceof KeyboardEvent) {
            if (!keyboardListenerEnabled) {
                return;
            }
        } else {
            if (!mouseListenerEnabled) {
                return;
            }
        }

        eventListeners.forEach(eventListener -> eventListener.newRemoteEvent(remoteEvent));
    }

    private void notifyQuit() {
        eventListeners.forEach(ScreenShareEventListener::quitButtonPressed);
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
        JPanel pageStartPanel = new JPanel();
        pageStartPanel.setLayout(new BoxLayout(pageStartPanel, BoxLayout.LINE_AXIS));
        contentPane.add(pageStartPanel, BorderLayout.PAGE_START);

        quitButton = new JButton("Quit");
        quitButton.addActionListener(action -> dispose());
        pageStartPanel.add(quitButton);

        nativeResolutionButton = new JButton("Set Native Resolution");
        nativeResolutionButton.addActionListener(action -> {
            setExtendedState(Frame.NORMAL);
            pack();
        });
        pageStartPanel.add(nativeResolutionButton);

        keyboardEnableButton = new JButton("Toggle Keyboard Events");
        keyboardEnableButton.addActionListener(action -> toggleKeyboardListener());
        pageStartPanel.add(keyboardEnableButton);

        mouseEnableButton = new JButton("Toggle Mouse Events");
        mouseEnableButton.addActionListener(action -> toggleMouseListener());
        pageStartPanel.add(mouseEnableButton);

        screenView = new ScreenView();
        contentPane.add(screenView, BorderLayout.CENTER);
    }

    private void toggleMouseListener() {
        if (mouseListenerEnabled) {
            mouseListenerEnabled = false;
            mouseEnableButton.setBackground(Color.RED);
        } else {
            mouseListenerEnabled = true;
            mouseEnableButton.setBackground(Color.GREEN);
        }
    }

    private void toggleKeyboardListener() {
        if (keyboardListenerEnabled) {
            keyboardListenerEnabled = false;
            keyboardEnableButton.setBackground(Color.RED);
        } else {
            keyboardListenerEnabled = true;
            keyboardEnableButton.setBackground(Color.GREEN);
        }
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
