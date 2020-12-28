package net.debreczeni.remotedesktop.ui;

import net.debreczeni.remotedesktop.model.socket.RemoteImage;
import net.debreczeni.remotedesktop.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ScreenView extends JLabel {

    public ScreenView() {
        setVisible(true);
    }

    public void updateImage(ImageIcon image) {
//        SwingUtilities.invokeLater(() -> setIcon(image));
        setIcon(image);
    }

    public void updateImage(RemoteImage image) {

        SwingUtilities.invokeLater(() -> {
            try {
                setIcon(new ImageIcon(image.get()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        ImageIcon icon = (ImageIcon) getIcon();
        if (icon != null) {
            ImageUtil.drawScaledImage(icon.getImage(), this, g, true);
        }
    }
}
