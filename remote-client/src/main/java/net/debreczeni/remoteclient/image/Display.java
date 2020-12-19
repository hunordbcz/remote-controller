package net.debreczeni.remoteclient.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public final class Display {
    private static final GraphicsDevice[] devices = Arrays
            .stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
            .sorted(Comparator.comparingInt(a -> a.getDefaultConfiguration().getBounds().x))
            .toArray(GraphicsDevice[]::new);

    private static int nr = 0;
    private static Rectangle screenRectangle;
    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        screenRectangle = devices[nr].getDefaultConfiguration().getBounds();
    }

    private Display(){

    }

    public static int getNr() {
        return nr;
    }

    public static void setNr(int nr) {
        Display.nr = nr;
        screenRectangle = devices[nr].getDefaultConfiguration().getBounds();
    }

    public static Point getPointByScreen(Point point, int screenNr) {
        int x = devices[screenNr].getDefaultConfiguration().getBounds().x;
        int y = devices[screenNr].getDefaultConfiguration().getBounds().y;

        return new Point(x + point.x, y + point.y);
    }

    public static BufferedImage takeScreenshot() {
        return robot.createScreenCapture(screenRectangle);
    }
}
