package net.debreczenichis.remotedesktop.model.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Data
@AllArgsConstructor
public class RemoteImage {
    private static final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private byte[] data;
    private long created;
    private BufferedImage bufferedImage;

    @SneakyThrows
    public RemoteImage(BufferedImage bufferedImage) {
        created = System.currentTimeMillis();

        ImageIO.write(bufferedImage, "JPG", byteArrayOutputStream);
        data = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.reset();
    }

    public BufferedImage get() throws IOException {
        if (bufferedImage != null) {
            return bufferedImage;
        }

        final InputStream is = new ByteArrayInputStream(data);
        return (bufferedImage = ImageIO.read(is));
    }

    public long getCreated() {
        return created;
    }
}
