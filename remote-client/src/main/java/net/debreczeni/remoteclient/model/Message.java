package net.debreczeni.remoteclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.debreczeni.remoteclient.image.Display;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
//        try {
//            BufferedImage image = Display.takeScreenshot();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(image, "JPG", baos);
//            byte[] bytes = baos.toByteArray();
//            this.image = bytes;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    public ImageIcon getImage() {
        if(image == null) return null;
        InputStream is = new ByteArrayInputStream(image);
        BufferedImage bi;
        try {
             bi = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new ImageIcon(bi);
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