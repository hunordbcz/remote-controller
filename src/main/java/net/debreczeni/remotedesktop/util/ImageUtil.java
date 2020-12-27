package net.debreczeni.remotedesktop.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ImageUtil {
    private final static double widthToHeightRatio = 0.5625D;

    private ImageUtil() {
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
//        int
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }
}
