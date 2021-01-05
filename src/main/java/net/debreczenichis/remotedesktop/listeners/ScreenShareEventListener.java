package net.debreczenichis.remotedesktop.listeners;

import net.debreczenichis.remotedesktop.model.socket.events.RemoteEvent;

public interface ScreenShareEventListener {
    void newRemoteEvent(RemoteEvent event);

    void quitButtonPressed();
}
