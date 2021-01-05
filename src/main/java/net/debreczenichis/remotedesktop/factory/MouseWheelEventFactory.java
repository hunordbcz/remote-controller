package net.debreczenichis.remotedesktop.factory;

import lombok.Data;
import net.debreczenichis.remotedesktop.model.socket.events.MouseWheelEvent;
import net.debreczenichis.remotedesktop.util.SingletonRobot;

@Data
public class MouseWheelEventFactory implements AbstractEventFactory {

    private final MouseWheelEvent event;

    @Override
    public void process() {
        SingletonRobot.getInstance().mouseWheel(event.getAmount());
    }
}
