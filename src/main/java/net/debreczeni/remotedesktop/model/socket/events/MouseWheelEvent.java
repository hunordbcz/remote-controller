package net.debreczeni.remotedesktop.model.socket.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MouseWheelEvent extends RemoteEvent{
    private int amount;

    public MouseWheelEvent() {
        super(EventType.MOUSE_SCROLL);
    }
}
