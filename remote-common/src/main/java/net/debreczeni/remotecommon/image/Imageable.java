package net.debreczeni.remotecommon.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

interface Imageable {
    BufferedImage takeScreenshot();
}
