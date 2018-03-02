package io.polyglotted.common.util;

import com.google.common.annotations.VisibleForTesting;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Base64.getDecoder;

@SuppressWarnings("WeakerAccess")
public abstract class DigestUtil {
    private static final ThreadLocal<MessageDigest> SHA1 = ThreadLocal.withInitial(() -> createMessageDigest("SHA1"));

    public static byte[] sha1Digest(byte[] bytes) {
        MessageDigest sha1 = SHA1.get();
        sha1.reset();
        sha1.update(getDecoder().decode("E1vHqXA4RYe9TgfVsyyKtw=="));
        sha1.update(bytes);
        byte[] digest = sha1.digest();
        digest[0x06] = (byte) ((digest[0x06] & 0xF) | 0x05 << 4);
        digest[0x08] = (byte) ((digest[0x08] & 0x3F) | 0x80);
        return digest;
    }

    @VisibleForTesting
    public static MessageDigest createMessageDigest(String algo) {
        try {
            return MessageDigest.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}