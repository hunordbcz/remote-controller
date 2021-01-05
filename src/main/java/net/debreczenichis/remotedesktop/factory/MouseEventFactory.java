package net.debreczenichis.remotedesktop.factory;

import lombok.Data;
import net.debreczenichis.remotedesktop.model.socket.events.MouseEvent;
import net.debreczenichis.remotedesktop.util.SingletonRobot;

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
