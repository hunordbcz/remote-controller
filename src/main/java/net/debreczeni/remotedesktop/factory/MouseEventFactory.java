package net.debreczeni.remotedesktop.factory;

import lombok.Data;
import net.debreczeni.remotedesktop.model.socket.events.MouseEvent;
import net.debreczeni.remotedesktop.util.SingletonRobot;

@Data
public class MouseEventFactory implements AbstractEventFactory {

    private final MouseEvent event;

    @Override
    public void process() {
        switch (event.getType()) {
            case PRESS:
                SingletonRobot.getInstance().mousePress(event.getButton());
                break;
            case RELEASE:
                SingletonRobot.getInstance().mouseRelease(event.getButton());
                break;
        }
    }
}
