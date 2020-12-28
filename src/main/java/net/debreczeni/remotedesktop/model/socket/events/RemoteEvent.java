package net.debreczeni.remotedesktop.model.socket.events;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RemoteEvent implements Serializable {
    public enum EventType {
        KEYBOARD_PRESS,
        KEYBOARD_RELEASE,
        MOUSE_PRESS,
        MOUSE_RELEASE,
        MOUSE_SCROLL,
        MOUSE_MOVE
    }

    private EventType event;

    protected RemoteEvent(EventType event) {
        this.event = event;
    }

    public EventType getEvent() {
        return event;
    }
}


