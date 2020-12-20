package net.debreczeni.remoteserver.image;

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

    private final Rectangle screenRectangle;
    private final int nr;
    private Robot robot;

    Display(Robot robot, int nr) throws AWTException {
        this(nr);

        this.robot = robot;
    }

    @SneakyThrows
    public Display(int nr) {
        if (nr > devices.length) {
            throw new IllegalArgumentException("Invalid display number");
        }

        this.nr = nr;

        screenRectangle = new Rectangle(devices[nr].getDefaultConfiguration().getBounds());
        robot = new Robot();
    }

    public int getNr() {
        return nr;
    }

    public static Point getPointByScreen(Point point, int screenNr) {
        int x = devices[screenNr].getDefaultConfiguration().getBounds().x;
        int y = devices[screenNr].getDefaultConfiguration().getBounds().y;

        return new Point(x + point.x, y + point.y);
    }

    public BufferedImage takeScreenshot(int nr){
        return robot.createScreenCapture(devices[nr].getDefaultConfiguration().getBounds());
    }

    @Override
    public BufferedImage takeScreenshot() {
        return robot.createScreenCapture(screenRectangle);
    }

    @Override
    public void saveScreenshot(String path) throws IOException {
        ImageIO.write(takeScreenshot(), "jpg", new File(path));
    }
}
