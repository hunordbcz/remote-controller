package net.debreczeni.remotedesktop.factory;

import lombok.Data;
import net.debreczeni.remotedesktop.image.Display;
import net.debreczeni.remotedesktop.model.socket.events.MouseMovementEvent;
import net.debreczeni.remotedesktop.util.SingletonRobot;

import java.awt.*;

@Data
public class MouseMovementEventFactory implements AbstractEventFactory {

    private final MouseMovementEvent event;

    @Override
    public void process() {
        Point point = Display.getPointByScreen(event.getPoint(), event.getScreenNr());
        SingletonRobot.getInstance().mouseMove(point.x, point.y);
    }
}
