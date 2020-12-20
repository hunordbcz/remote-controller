package net.debreczeni.remoteserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.debreczeni.remoteserver.image.Display;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

@Data
@AllArgsConstructor
//@NoArgsConstructor
public class Message {
    private String origin;
    private String interaction;
    private long index;
    private long created = System.currentTimeMillis();
    private byte[] image;

    public Message(){
        try {
            Display display = new Display(0);
            BufferedImage image = display.takeScreenshot();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "JPG", baos);
            byte[] bytes = baos.toByteArray();
            this.image = bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message(String origin, String interaction) {
        this();
        this.origin = origin;
        this.interaction = interaction;
        this.index = 0;
    }

    public Message(String origin, String interaction, long index) {
        this();
        this.origin = origin;
        this.interaction = interaction;
        this.index = index;
    }

    @Override
    public String toString() {
        return "Message{" +
                "origin='" + origin + '\'' +
                ", interaction='" + interaction + '\'' +
                ", index=" + index +
                ", created=" + created +
                '}';
    }
}