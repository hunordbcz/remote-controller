package net.debreczeni.remotedesktop.factory;

import net.debreczeni.remotedesktop.model.socket.events.*;

@FunctionalInterface
public interface AbstractEventFactory {

    void process();

    static AbstractEventFactory getFactory(RemoteEvent event) {
        switch (event.getEvent()) {
            case KEYBOARD_PRESS:
            case KEYBOARD_RELEASE:
                return new KeyboardEventFactory((KeyboardEvent) event);
            case MOUSE_PRESS:
            case MOUSE_RELEASE:
                return new MouseEventFactory((MouseEvent) event);
            case MOUSE_SCROLL:
                return new MouseWheelEventFactory((MouseWheelEvent) event);
            case MOUSE_MOVE:
                return new MouseMovementEventFactory((MouseMovementEvent) event);
            default:
                throw new IllegalArgumentException();
        }
    }
}
