package net.debreczeni.remotedesktop.model.socket.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.awt.event.InputEvent;

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
        this.button = switch (button){
            case 1 -> InputEvent.BUTTON1_DOWN_MASK;
            case 2 -> InputEvent.BUTTON2_DOWN_MASK;
            case 3 -> InputEvent.BUTTON3_DOWN_MASK;
            default -> throw new IllegalStateException("Unexpected value: " + button);
        };

        this.type = type;
        switch (type) {
            case PRESS -> super.setEvent(EventType.MOUSE_PRESS);
            case RELEASE -> super.setEvent(EventType.MOUSE_RELEASE);
        }
    }
}
