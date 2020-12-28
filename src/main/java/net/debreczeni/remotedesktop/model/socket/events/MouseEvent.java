package net.debreczeni.remotedesktop.model.socket.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
public class MouseEvent extends RemoteEvent {
    public enum TYPE {
        PRESS,
        RELEASE
    }

    private int button;
    private TYPE type;

    public MouseEvent(TYPE type, int button) {
        this.button = button;
        this.type = type;

        switch (type) {
            case PRESS -> super.setEvent(EventType.MOUSE_PRESS);
            case RELEASE -> super.setEvent(EventType.MOUSE_RELEASE);
        }
    }
}
