package net.debreczeni.remoteclient.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

interface Imageable {
    BufferedImage takeScreenshot();

    void saveScreenshot(String path) throws IOException;
}
