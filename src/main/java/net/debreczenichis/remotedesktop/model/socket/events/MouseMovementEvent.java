package net.debreczenichis.remotedesktop.model.socket.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class MouseMovementEvent extends RemoteEvent {
    private final int screenNr;
    private final Point point;

    public MouseMovementEvent(Point point, int screenNr) {
        super(EventType.MOUSE_MOVE);
        this.point = point;
        this.screenNr = screenNr;
    }
}