package net.debreczenichis.remotedesktop.security;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class TripleDes {

    protected static byte[] encrypt(byte[] bMessage, byte[] bKey, String transformation) {
        try {
            final SecretKey secretKey = new SecretKeySpec(bKey, "DESede");
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final byte[] bCipherMessage = cipher.doFinal(bMessage);
            return bCipherMessage;
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("No Such Padding", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No Such Algorithm", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid Key", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("Invalid Key", e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Invalid Key", e);
        }
    }

    protected static byte[] decrypt(byte[] bCipherMessage, byte[] bKey, String transformation) {
        try {
            final SecretKey secretKey = new SecretKeySpec(bKey, "DESede");
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            final byte[] bMessage = cipher.doFinal(bCipherMessage);
            return bMessage;
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("No Such Padding", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No Such Algorithm", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid Key", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("Invalid Key", e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Invalid Key", e);
        }
    }

    protected static byte[] makeupKey(String hexKey) {
        byte[] keyBytes;
        try {
            keyBytes = Hex.decodeHex(hexKey.toCharArray());
            final byte[] encryptionKey = Arrays.copyOf(keyBytes, 24);
            if (keyBytes.length == 16) {
                for (int j = 0, k = 16; j < 8; ) {
                    encryptionKey[k++] = keyBytes[j++];
                }
            }
            return encryptionKey;
        } catch (DecoderException e) {
            throw new RuntimeException("Hex decoder failed!", e);
        }
    }

    protected static byte[] buildKey(String keySeed) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] digestOfPassword = md.digest(keySeed.getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            return keyBytes;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unsupported digest algorithm", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding", e);
        }
    }
}