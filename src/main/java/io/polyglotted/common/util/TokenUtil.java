package io.polyglotted.common.util;

import java.math.BigInteger;
import java.security.SecureRandom;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class TokenUtil {
    private static final SecureRandom random = new SecureRandom();

    public static String uniqueToken() { return uniqueToken(130); }

    public static String uniqueToken(int maxBits) { return new BigInteger(maxBits, random).toString(32).toLowerCase(); }

}