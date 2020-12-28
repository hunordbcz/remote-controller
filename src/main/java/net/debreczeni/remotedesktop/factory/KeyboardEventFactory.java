package net.debreczeni.remotedesktop.factory;

import lombok.Data;
import net.debreczeni.remotedesktop.model.socket.events.KeyboardEvent;
import net.debreczeni.remotedesktop.util.SingletonRobot;

@Data
public class KeyboardEventFactory implements AbstractEventFactory {

    private final KeyboardEvent event;

    @Override
    public void process() {
        switch (event.getType()) {
            case PRESS -> SingletonRobot.getInstance().keyPress(event.getKeyCode());
            case RELEASE -> SingletonRobot.getInstance().keyRelease(event.getKeyCode());
        }
    }
}
