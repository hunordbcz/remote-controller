package net.debreczeni.remotecommon.model.socket.events;

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

    public KeyboardEvent(TYPE type) {
        switch (type) {
            case PRESS -> super.setEvent(EventType.KEYBOARD_PRESS);
            case RELEASE -> super.setEvent(EventType.KEYBOARD_RELEASE);
        }
    }
}
