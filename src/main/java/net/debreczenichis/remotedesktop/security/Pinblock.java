package net.debreczenichis.remotedesktop.security;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class Pinblock {

    protected static String encode(String pin, String pan, byte[] key) {
        try {
            final String pinLenHead = StringUtils.leftPad(Integer.toString(pin.length()), 2, '0') + pin;
            final String pinData = StringUtils.rightPad(pinLenHead, 16, 'F');
            final byte[] bPin = Hex.decodeHex(pinData.toCharArray());
            final String panPart = extractPanAccountNumberPart(pan);
            final String panData = StringUtils.leftPad(panPart, 16, '0');
            final byte[] bPan = Hex.decodeHex(panData.toCharArray());

            final byte[] pinblock = new byte[8];
            for (int i = 0; i < 8; i++)
                pinblock[i] = (byte) (bPin[i] ^ bPan[i]);

            final byte[] extendedPinblock = TripleDes.encrypt(pinblock, key, "DESede/ECB/NoPadding");

            return Hex.encodeHexString(extendedPinblock).toUpperCase();

        } catch (DecoderException e) {
            throw new RuntimeException("Hex decoder failed!", e);
        }
    }

    protected static String extractPanAccountNumberPart(String accountNumber) {
        String accountNumberPart = null;
        if (accountNumber.length() > 12)
            accountNumberPart = accountNumber.substring(accountNumber.length() - 13, accountNumber.length() - 1);
        else
            accountNumberPart = accountNumber;
        return accountNumberPart;
    }

    protected static String decode(String extendedPinBlock, String pan, byte[] key) {
        try {

            final byte[] bExtendedPinblock = Hex.decodeHex(extendedPinBlock.toCharArray());
            final byte[] bPinBlock = TripleDes.decrypt(bExtendedPinblock, key, "DESede/ECB/NoPadding");

            final String panPart = extractPanAccountNumberPart(pan);
            final String panData = StringUtils.leftPad(panPart, 16, '0');
            final byte[] bPan = Hex.decodeHex(panData.toCharArray());

            final byte[] bPin = new byte[8];
            for (int i = 0; i < 8; i++)
                bPin[i] = (byte) (bPinBlock[i] ^ bPan[i]);

            final String pinData = Hex.encodeHexString(bPin);
            final int pinLen = Integer.parseInt(pinData.substring(0, 2));
            return pinData.substring(2, 2 + pinLen);

        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid pinblock format!");
        } catch (DecoderException e) {
            throw new RuntimeException("Hex decoder failed!", e);
        }
    }
}