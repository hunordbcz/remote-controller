package net.debreczeni.remoteclient.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Application implements Imageable {
    @Override
    public BufferedImage takeScreenshot() {
        return null;
    }

    @Override
    public void saveScreenshot(String path) throws IOException {

    }
}
