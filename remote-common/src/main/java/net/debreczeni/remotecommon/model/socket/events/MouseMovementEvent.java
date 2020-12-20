package net.debreczeni.remotecommon.model.socket.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class MouseMovementEvent extends RemoteEvent {
    private Point point;

    public MouseMovementEvent() {
        super(EventType.MOUSE_MOVE);
    }
}