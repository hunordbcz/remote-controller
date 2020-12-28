package net.debreczeni.remotedesktop.factory;

import lombok.Data;
import net.debreczeni.remotedesktop.model.socket.events.MouseWheelEvent;
import net.debreczeni.remotedesktop.util.SingletonRobot;

@Data
public class MouseWheelEventFactory implements AbstractEventFactory {

    private final MouseWheelEvent event;

    @Override
    public void process() {
        SingletonRobot.getInstance().mouseWheel(event.getAmount());
    }
}
