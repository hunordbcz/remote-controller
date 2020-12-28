package net.debreczeni.remotedesktop.model.socket.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyboardEvent extends RemoteEvent {
    public enum TYPE {
        PRESS,
        RELEASE
    }

    private int keyCode;
    private TYPE type;

    public KeyboardEvent(TYPE type, int keyCode) {
        this.keyCode = keyCode;
        this.type = type;

        switch (type) {
            case PRESS -> super.setEvent(EventType.KEYBOARD_PRESS);
            case RELEASE -> super.setEvent(EventType.KEYBOARD_RELEASE);
        }
    }
}
