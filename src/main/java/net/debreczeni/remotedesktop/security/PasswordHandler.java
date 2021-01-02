package net.debreczeni.remotedesktop.security;

import net.debreczeni.remotedesktop.model.User;
import net.debreczeni.remotedesktop.util.InetAddress;

public class PasswordHandler {
    private static final String viewPin = User.getInstance().getViewToken();
    private static final String controlPin = User.getInstance().getControlToken();
    private final String host;

    public PasswordHandler() {
        this(InetAddress.getLocalAddress());
    }

    public PasswordHandler(String host) {
        this.host = host;
    }

    public String encrypt(String text, TYPE type) {
        return Pinblock.encode(text, host.replaceAll("\\.", "0"), type.bKey);
    }

    public String getEncryptedViewPin() {
        return Pinblock.encode(viewPin, host.replaceAll("\\.", "0"), TYPE.VIEW.bKey);
    }

    public String getEncryptedControlPin() {
        return Pinblock.encode(controlPin, host.replaceAll("\\.", "0"), TYPE.CONTROL.bKey);
    }

    public enum TYPE {
        VIEW(TripleDes.buildKey("view")),
        CONTROL(TripleDes.buildKey("control"));

        private final byte[] bKey;

        TYPE(byte[] bKey) {
            this.bKey = bKey;
        }
    }
}
