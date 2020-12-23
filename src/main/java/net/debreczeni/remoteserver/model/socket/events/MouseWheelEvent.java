package net.debreczeni.remoteserver.model.socket.events;

public class MouseWheelEvent extends RemoteEvent{
    private int amount;

    public MouseWheelEvent() {
        super(EventType.MOUSE_SCROLL);
    }
}
