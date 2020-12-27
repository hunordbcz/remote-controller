package net.debreczeni.remotedesktop.image;

import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class Display implements Imageable {
    private static final GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final Rectangle[] devicesBounds = Arrays
            .stream(g.getScreenDevices())
            .map(graphicsDevice -> graphicsDevice.getDefaultConfiguration().getBounds())
            .sorted(Comparator.comparingInt(a -> a.x))
            .toArray(Rectangle[]::new);
    private static Display single_instance = null;
    private final Robot robot;
    private Rectangle screenRectangle;
    private int nr = 0;

    private Display() throws AWTException {
        robot = new Robot();
        screenRectangle = devicesBounds[nr];
    }

    @SneakyThrows
    public static Display getInstance(int nr) {
        if (single_instance == null) {
            single_instance = new Display();
        }

        if (single_instance.getNr() != nr) {
            single_instance.setNr(nr);
        }

        return single_instance;
    }

    public static int getDisplayNumbers() {
        return devicesBounds.length;
    }

    public static Rectangle[] getDevicesBounds() {
        return devicesBounds;
    }

    public static Point getPointByScreen(Point point, int screenNr) {
        Rectangle bounds = devicesBounds[screenNr];
        int x = bounds.x;
        int y = bounds.y;

        return new Point(x + point.x, y + point.y);
    }

    public int getNr() {
        return nr;
    }

    private void setNr(int nr) {
        if (nr > devicesBounds.length) {
            throw new IllegalArgumentException("Invalid display number");
        }

        this.nr = nr;
        this.screenRectangle = devicesBounds[nr];
    }

    @Override
    public BufferedImage takeScreenshot() {
        return robot.createScreenCapture(screenRectangle);
    }
}
