package net.debreczenichis.remotedesktop.factory;

import lombok.Data;
import net.debreczenichis.remotedesktop.image.Display;
import net.debreczenichis.remotedesktop.model.socket.events.MouseMovementEvent;
import net.debreczenichis.remotedesktop.util.SingletonRobot;

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
