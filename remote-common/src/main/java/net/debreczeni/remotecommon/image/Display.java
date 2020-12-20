package net.debreczeni.remotecommon.image;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Display implements Imageable {
    private static final GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static GraphicsDevice[] devices = Arrays
            .stream(g.getScreenDevices())
            .sorted(Comparator.comparingInt(a -> a.getDefaultConfiguration().getBounds().x))
            .toArray(GraphicsDevice[]::new);

    private Rectangle screenRectangle;
    private int nr = 0;
    private final Robot robot;

    private static Display single_instance = null;

    private Display() throws AWTException {
        robot = new Robot();
        screenRectangle = devices[nr].getDefaultConfiguration().getBounds();
    }

    @SneakyThrows
    public static Display getInstance(int nr) {
        if (single_instance == null){
            single_instance = new Display();
        }

        if(single_instance.getNr() != nr){
            single_instance.setNr(nr);
        }

        return single_instance;
    }

    private void setNr(int nr){
        if (nr > devices.length) {
            throw new IllegalArgumentException("Invalid display number");
        }

        this.nr = nr;
        this.screenRectangle = devices[nr].getDefaultConfiguration().getBounds();
    }

    public int getNr() {
        return nr;
    }

    public static Point getPointByScreen(Point point, int screenNr) {
        Rectangle bounds = devices[screenNr].getDefaultConfiguration().getBounds();
        int x = bounds.x;
        int y = bounds.y;

        return new Point(x + point.x, y + point.y);
    }

    @Override
    public BufferedImage takeScreenshot() {
        return robot.createScreenCapture(screenRectangle);
    }
}
