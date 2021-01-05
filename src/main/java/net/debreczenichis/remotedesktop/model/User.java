package net.debreczenichis.remotedesktop.model;

import lombok.Getter;
import lombok.SneakyThrows;
import net.debreczenichis.remotedesktop.util.InetAddress;

import java.io.Serializable;
import java.util.Random;

@Getter
public class User implements Serializable {
    private final static int LOGIN_TOKEN_LENGTH = 6;
    private static User single_instance = null;
    private final String name;
    private final String viewToken;
    private final String controlToken;

    private User() {
        final Random random = new Random();
        name = InetAddress.getHostName();
        viewToken = String.valueOf(random.nextInt(90000) + 10000);
        controlToken = String.valueOf(random.nextInt(90000) + 10000);
    }

    @SneakyThrows
    public static User getInstance() {
        if (single_instance == null) {
            single_instance = new User();
        }

        return single_instance;
    }
}
