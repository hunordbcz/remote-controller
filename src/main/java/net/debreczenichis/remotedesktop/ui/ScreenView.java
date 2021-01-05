package net.debreczenichis.remotedesktop.ui;

import net.debreczenichis.remotedesktop.model.socket.RemoteImage;
import net.debreczenichis.remotedesktop.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ScreenView extends JLabel {

    private Rectangle imageRectangle;

    public ScreenView() {
        setVisible(true);
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
            imageRectangle = ImageUtil.drawScaledImage(icon.getImage(), this, g, true);
        }
    }

    public Point getPointOnImage(Point point) {
        if (imageRectangle == null ||
                point.x < imageRectangle.x ||
                point.y < imageRectangle.y ||
                point.x > (imageRectangle.x + imageRectangle.width) ||
                point.y > (imageRectangle.y + imageRectangle.height)) {
            return null;
        }

        return new Point(point.x - imageRectangle.x, point.y - imageRectangle.y);
    }

    public Point getPointOnScreen(Point pointOnImage, Dimension nativeSize) {
        int x = (nativeSize.width * pointOnImage.x) / imageRectangle.width;
        int y = (nativeSize.height * pointOnImage.y) / imageRectangle.height;

        return new Point(x, y);
    }
}
