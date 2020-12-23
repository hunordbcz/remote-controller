package net.debreczeni.remotedesktop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String origin;
    private String interaction;
    private long index;
    private long created = System.currentTimeMillis();

    public Message(String origin, String interaction) {
        this.origin = origin;
        this.interaction = interaction;
        this.index = 0;
    }

    public Message(String origin, String interaction, long index) {
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