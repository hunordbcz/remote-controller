package net.debreczenichis.remotedesktop.factory;

import lombok.Data;
import net.debreczenichis.remotedesktop.model.socket.events.KeyboardEvent;
import net.debreczenichis.remotedesktop.util.SingletonRobot;

@Data
public class KeyboardEventFactory implements AbstractEventFactory {

    private final KeyboardEvent event;

    @Override
    public void process() {
        switch (event.getType()) {
            case PRESS:
                SingletonRobot.getInstance().keyPress(event.getKeyCode());
                break;
            case RELEASE:
                SingletonRobot.getInstance().keyRelease(event.getKeyCode());
                break;
        }
    }
}
