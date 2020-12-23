package net.debreczeni.remotedesktop.model.socket.events;

public class MouseWheelEvent extends RemoteEvent{
    private int amount;

    public MouseWheelEvent() {
        super(EventType.MOUSE_SCROLL);
    }
}
