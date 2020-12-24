package net.debreczeni.remotedesktop.ui;

import net.debreczeni.remotedesktop.model.socket.RemoteImage;

import javax.swing.*;
import java.io.IOException;

public class ScreenView extends JLabel {

    public ScreenView() {
        setVisible(true);
    }

    public void updateImage(ImageIcon image){
//        SwingUtilities.invokeLater(() -> setIcon(image));
        setIcon(image);
    }

    public void updateImage(RemoteImage image){
        SwingUtilities.invokeLater(()-> {
            try {
                setIcon(image.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
