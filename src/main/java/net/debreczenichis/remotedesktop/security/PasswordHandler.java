package net.debreczenichis.remotedesktop.security;

import net.debreczenichis.remotedesktop.model.User;

public class PasswordHandler {
    private static final String viewPin = User.getInstance().getViewToken();
    private static final String controlPin = User.getInstance().getControlToken();
    private final String host;

    public PasswordHandler() {
        this("123456789");
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
