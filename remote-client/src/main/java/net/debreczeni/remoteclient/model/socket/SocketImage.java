package net.debreczeni.remoteclient.model.socket;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Data
@AllArgsConstructor
public class SocketImage {
    private static final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private byte[] data;
    private long created;

    public SocketImage(BufferedImage bufferedImage) throws IOException {
        created = System.currentTimeMillis();

        ImageIO.write(bufferedImage, "JPG", byteArrayOutputStream);
        data = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.reset();
    }

    public ImageIcon get() throws IOException {
        final InputStream is = new ByteArrayInputStream(data);
        BufferedImage bi = ImageIO.read(is);
        return new ImageIcon(bi);
    }

    public long getCreated(){
        return created;
    }
}
