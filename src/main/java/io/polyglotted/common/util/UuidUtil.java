//adapted from https://github.com/FasterXML/jackson-databind/blob/master/src/main/java/com/fasterxml/jackson/databind/deser/std/UUIDDeserializer.java
package io.polyglotted.common.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

import static io.polyglotted.common.util.Assertions.checkBool;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class UuidUtil {
    private static final int[] HEX_DIGITS = new int[127];

    static {
        Arrays.fill(HEX_DIGITS, -1);
        for (int i = 0; i < 10; ++i) {
            HEX_DIGITS['0' + i] = i;
        }
        for (int i = 0; i < 6; ++i) {
            HEX_DIGITS['a' + i] = 10 + i;
            HEX_DIGITS['A' + i] = 10 + i;
        }
    }

    public static String genUuidStr(String name) { return generateUuid(name.getBytes(UTF_8)).toString().toLowerCase(); }

    public static UUID generateUuid(byte[] nameBytes) { return uuidFrom(DigestUtils.sha1(nameBytes)); }

    public static UUID uuidFrom(String uuid) {
        long[] bits = gatherLong(uuid);
        return new UUID(bits[0], bits[1]);
    }

    public static UUID uuidFrom(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    public static byte[] toBytes(String uuid) {
        long[] bits = gatherLong(uuid);
        return getBytes(bits[0], bits[1]);
    }

    public static byte[] toBytes(UUID uuid) { return getBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()); }

    private static long[] gatherLong(String id) {
        checkBool(requireNonNull(id, "Null string passed").length() == 36, "UUID has to be represented by 36-char representation");
        if ((id.charAt(8) != '-') || (id.charAt(13) != '-')
            || (id.charAt(18) != '-') || (id.charAt(23) != '-')) {
            throw new NumberFormatException("UUID has to be represented by the standard 36-char representation");
        }
        long l1 = ((long) intFromChars(id, 0)) << 32;
        long l2 = (((long) shortFromChars(id, 9)) << 16) | shortFromChars(id, 14);
        long l3 = ((long) (shortFromChars(id, 19) << 16) | shortFromChars(id, 24)) << 32;
        long l4 = ((long) intFromChars(id, 28) << 32) >>> 32;
        return new long[]{l1 + l2, l3 | l4};
    }

    private static byte[] getBytes(long mostSignificantBits, long leastSignificantBits) {
        return ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN).putLong(mostSignificantBits)
            .putLong(leastSignificantBits).array();
    }

    private static int intFromChars(String str, int index) {
        return (byteFromChars(str, index) << 24) + (byteFromChars(str, index + 2) << 16)
            + (byteFromChars(str, index + 4) << 8) + byteFromChars(str, index + 6);
    }

    private static int shortFromChars(String str, int index) { return (byteFromChars(str, index) << 8) + byteFromChars(str, index + 2); }

    private static int byteFromChars(String str, int index) {
        final char c1 = str.charAt(index);
        final char c2 = str.charAt(index + 1);
        if (c1 <= 127 && c2 <= 127) {
            int hex = (HEX_DIGITS[c1] << 4) | HEX_DIGITS[c2];
            if (hex >= 0) {
                return hex;
            }
        }
        throw (c1 > 127 || HEX_DIGITS[c1] < 0) ? badChar(str, c1) : badChar(str, c2);
    }

    private static NumberFormatException badChar(String uuidStr, char c) {
        return new NumberFormatException("Non-hex character '" + c + "', not valid character for a UUID String"
            + "' (value 0x" + Integer.toHexString(c) + ") for UUID String \"" + uuidStr + "\"");
    }
}