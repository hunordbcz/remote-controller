package net.debreczeni.remoteclient.controller;

import lombok.Getter;
import net.debreczeni.remoteclient.model.Client;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
public class ClientHandler {
    private final static int LOGIN_ID_LENGTH = 6;
    private final static int LOGIN_TOKEN_LENGTH = 6;

    private final Client client;

    private final String loginToken;

    public ClientHandler(Client client){
        this.client = client;
        this.loginToken = RandomStringUtils.randomAlphabetic(LOGIN_TOKEN_LENGTH);
    }

    public String getLoginToken() {
        return loginToken;
    }

}
