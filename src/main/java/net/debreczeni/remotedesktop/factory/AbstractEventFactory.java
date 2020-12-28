package net.debreczeni.remotedesktop.factory;

import net.debreczeni.remotedesktop.model.socket.events.*;

@FunctionalInterface
public interface AbstractEventFactory {

    void process();

    static AbstractEventFactory getFactory(RemoteEvent event) {
        return switch (event.getEvent()) {
            case KEYBOARD_PRESS, KEYBOARD_RELEASE -> new KeyboardEventFactory((KeyboardEvent) event);
            case MOUSE_PRESS, MOUSE_RELEASE -> new MouseEventFactory((MouseEvent) event);
            case MOUSE_SCROLL -> new MouseWheelEventFactory((MouseWheelEvent) event);
            case MOUSE_MOVE -> new MouseMovementEventFactory((MouseMovementEvent) event);
        };
    }
}
