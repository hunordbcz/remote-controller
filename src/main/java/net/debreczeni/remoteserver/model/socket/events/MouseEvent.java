package net.debreczeni.remoteserver.model.socket.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MouseEvent extends RemoteEvent {
    public enum TYPE {
        PRESS,
        RELEASE
    }

    public MouseEvent(TYPE type) {
        switch (type) {
            case PRESS -> super.setEvent(EventType.MOUSE_PRESS);
            case RELEASE -> super.setEvent(EventType.MOUSE_RELEASE);
        }
    }
}
