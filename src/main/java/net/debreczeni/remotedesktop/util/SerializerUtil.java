package net.debreczeni.remotedesktop.util;

import java.io.*;
import java.util.Base64;

public final class SerializerUtil {
    private SerializerUtil() {
    }

    public static <T extends Serializable> T fromString(String content) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(content);
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data))) {
            return (T) ois.readObject();
        }
    }

    public static String toString(Serializable o) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }
}
