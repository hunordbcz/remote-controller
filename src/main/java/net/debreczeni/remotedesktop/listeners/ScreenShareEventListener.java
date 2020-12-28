package net.debreczeni.remotedesktop.listeners;

import net.debreczeni.remotedesktop.model.socket.events.RemoteEvent;

public interface ScreenShareEventListener {
    void newRemoteEvent(RemoteEvent event);

    void quitButtonPressed();
}
