package net.debreczeni.remoteclient.model;

import lombok.Getter;

@Getter
public class Client {
    private final String name;

    public Client(String name) {
        this.name = name;
    }
}
