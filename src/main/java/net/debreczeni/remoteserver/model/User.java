package net.debreczeni.remoteserver.model;

import lombok.Getter;
import lombok.SneakyThrows;
import net.debreczeni.remoteserver.util.InetAddress;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;

@Getter
public class User implements Serializable {
    private final static int LOGIN_TOKEN_LENGTH = 6;

    private final String name;
    private final String viewToken;
    private final String controlToken;

    private User() {
        name = InetAddress.getHostName();
        viewToken = RandomStringUtils.randomAlphabetic(LOGIN_TOKEN_LENGTH);
        controlToken = RandomStringUtils.randomAlphabetic(LOGIN_TOKEN_LENGTH);
    }

    private static User single_instance = null;

    @SneakyThrows
    public static User getInstance() {
        if (single_instance == null){
            single_instance = new User();
        }

        return single_instance;
    }
}
