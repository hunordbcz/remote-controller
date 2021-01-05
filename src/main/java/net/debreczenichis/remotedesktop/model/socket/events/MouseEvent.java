package net.debreczenichis.remotedesktop.model.socket.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.event.InputEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class MouseEvent extends RemoteEvent {
    private int button;
    private TYPE type;

    public MouseEvent(TYPE type, int button) {
        switch (button) {
            case 1:
                this.button = InputEvent.BUTTON1_DOWN_MASK;
                break;
            case 2:
                this.button = InputEvent.BUTTON2_DOWN_MASK;
                break;
            case 3:
                this.button = InputEvent.BUTTON3_DOWN_MASK;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button);
        }

        this.type = type;
        switch (type) {
            case PRESS:
                super.setEvent(EventType.MOUSE_PRESS);
                break;
            case RELEASE:
                super.setEvent(EventType.MOUSE_RELEASE);
                break;
        }
    }

    public enum TYPE {
        PRESS,
        RELEASE
    }
}
