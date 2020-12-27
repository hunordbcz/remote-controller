package net.debreczeni.remotedesktop.ui;

import net.debreczeni.remotedesktop.adapter.MouseAdapter;
import net.debreczeni.remotedesktop.model.socket.RemoteImage;

import javax.swing.*;
import java.awt.*;

public class ScreenShare extends JPanel {
    private ScreenView screenView;

    public ScreenShare() {
        initComponents();
    }

    private void initComponents() {
        screenView = new ScreenView();
        setLayout(new GridLayout());
        add(screenView);

        MouseAdapter mouseAdapter = new MouseAdapter(screenView);
        screenView.addMouseMotionListener(mouseAdapter);
        screenView.addMouseWheelListener(mouseAdapter);
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
        screenView.setSize(width, height);
    }

    public ScreenView getScreenView() {
        return screenView;
    }

    public void updateImage(RemoteImage image) {
        screenView.updateImage(image);
    }

}
