package net.debreczeni.remoteclient.ui.elements;

import net.debreczeni.remoteclient.ui.MouseAdapter;
import net.debreczeni.remotecommon.model.socket.RemoteImage;

import javax.swing.*;

public class ScreenShare extends JPanel {
    private ScreenView screenView;

    public ScreenShare() {

        initComponents();
    }

    private void initComponents() {
        screenView = new ScreenView();
        add(screenView);
        JButton button = new JButton("Test");
        add(button);

        MouseAdapter mouseAdapter = new MouseAdapter(screenView);
        screenView.addMouseMotionListener(mouseAdapter);
        screenView.addMouseWheelListener(mouseAdapter);
    }

    public ScreenView getScreenView() {
        return screenView;
    }

    public void updateImage(RemoteImage image){
        screenView.updateImage(image);
    }

}
