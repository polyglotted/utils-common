package io.polyglotted.common.util;

import com.google.common.net.InetAddresses;
import io.polyglotted.common.model.GeoHash;
import io.polyglotted.common.model.GeoPoint;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static io.polyglotted.common.model.GeoPoint.geoPointFromString;
import static io.polyglotted.common.util.DateFormatters.*;
import static io.polyglotted.common.util.EncodingUtil.decodeBase64;
import static io.polyglotted.common.util.EncodingUtil.encodeBase64;
import static io.polyglotted.common.util.ReflectionUtil.safeClass;
import static java.time.ZoneOffset.UTC;

@SuppressWarnings("WeakerAccess")
public abstract class ConversionUtil {
    private static final DateTimeFormatter ZDT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSz");

    public static boolean asBool(Object value) {
        if (value instanceof Boolean) { return (Boolean) value; }
        else if (value instanceof String) { return "true".equalsIgnoreCase((String) value); }
        else throw new IllegalArgumentException("unknown boolean trait " + value + ":" + safeClass(value));
    }

    public static byte[] asBinary(Object value) {
        if (value instanceof byte[]) { return (byte[]) value; }
        else if (value instanceof ByteBuffer) { return ((ByteBuffer) value).array(); }
        else if (value instanceof String) { return decodeBase64((String) value); }
        else throw new IllegalArgumentException("unknown binary trait " + value + ":" + safeClass(value));
    }

    public static ByteBuffer asBuffer(Object value) {
        if (value instanceof ByteBuffer) { return (ByteBuffer) value; }
        else if (value instanceof byte[]) { return ByteBuffer.wrap((byte[]) value); }
        else if (value instanceof String) { return ByteBuffer.wrap(decodeBase64((String) value)); }
        else throw new IllegalArgumentException("unknown buffer trait " + value + ":" + safeClass(value));
    }

    public static GeoPoint asGeoPoint(Object value) {
        if (value instanceof GeoPoint) { return (GeoPoint) value; }
        else if (value instanceof GeoHash) { return ((GeoHash) value).point; }
        else if (value instanceof String) { return geoPointFromString((String) value); }
        else throw new IllegalArgumentException("unknown geopoint trait " + value + ":" + safeClass(value));
    }

    public static String asBinaryString(Object value) {
        if (value instanceof ByteBuffer) { return encodeBase64(((ByteBuffer) value).array()); }
        else if (value instanceof byte[]) { return encodeBase64((byte[]) value); }
        else if (value instanceof String) { return (String) value; }
        else throw new IllegalArgumentException("unknown binary trait " + value + ":" + safeClass(value));
    }

    public static UUID asUuid(Object value) {
        if (value instanceof UUID) { return (UUID) value; }
        else if (value instanceof String) { return UuidUtil.uuidFrom(String.valueOf(value)); }
        else throw new IllegalArgumentException("unknown uuid trait " + value + ":" + safeClass(value));
    }

    public static InetAddress asInetAddress(Object value) {
        if (value instanceof InetAddress) { return (InetAddress) value; }
        else if (value instanceof String) { return InetAddresses.forString((String) value); }
        else throw new IllegalArgumentException("unknown ipaddress trait " + value + ":" + safeClass(value));
    }

    @SneakyThrows public static URL asUrl(Object value) {
        if (value instanceof URL) { return (URL) value; }
        else if (value instanceof String) { return new URL((String) value); }
        else throw new IllegalArgumentException("unknown url trait " + value + ":" + safeClass(value));
    }

    @SneakyThrows public static URI asUri(Object value) {
        if (value instanceof URI) { return (URI) value; }
        else if (value instanceof String) { return new URI((String) value); }
        else throw new IllegalArgumentException("unknown uri trait " + value + ":" + safeClass(value));
    }

    public static LocalDate asLocalDate(Object value) {
        if (value instanceof LocalDate) { return (LocalDate) value; }
        else if (value instanceof LocalDateTime) { return ((LocalDateTime) value).toLocalDate(); }
        else if (value instanceof String) { return parseDate((String) value); }
        else if (value instanceof Date) { return ((Date) value).toInstant().atOffset(UTC).toLocalDate(); }
        else throw new IllegalArgumentException("unknown local date trait " + value + ":" + safeClass(value));
    }

    public static LocalTime asLocalTime(Object value) {
        if (value instanceof LocalTime) { return (LocalTime) value; }
        else if (value instanceof OffsetTime) { return ((OffsetTime) value).toLocalTime(); }
        else if (value instanceof Integer) { return LocalTime.ofSecondOfDay(((Integer) value).longValue()); }
        else if (value instanceof Long) { return LocalTime.ofNanoOfDay((Long) value); }
        else if (value instanceof String) { return parseTime((String) value); }
        else throw new IllegalArgumentException("unknown local time trait " + value + ":" + safeClass(value));
    }

    public static OffsetTime asOffsetTime(Object value) {
        if (value instanceof OffsetTime) { return (OffsetTime) value; }
        else if (value instanceof LocalTime) { return ((LocalTime) value).atOffset(UTC); }
        else if (value instanceof String) { return parseTime((String) value).atOffset(UTC); }
        else throw new IllegalArgumentException("unknown offset time trait " + value + ":" + safeClass(value));
    }

    public static String asZonedDateTimeString(Object value) { return ZDT_FORMATTER.format(asZonedDateTime(value)); }

    public static ZonedDateTime asZonedDateTime(Object value) {
        if (value instanceof ZonedDateTime) { return (ZonedDateTime) value; }
        else if (value instanceof LocalDateTime) { return ((LocalDateTime) value).atZone(UTC); }
        else if (value instanceof OffsetDateTime) { return ((OffsetDateTime) value).toZonedDateTime(); }
        else if (value instanceof String) { return parseDateTime((String) value); }
        else if (value instanceof Long) { return ZonedDateTime.ofInstant(Instant.ofEpochMilli((Long) value), UTC); }
        else if (value instanceof Date) { return ((Date) value).toInstant().atZone(UTC); }
        else throw new IllegalArgumentException("unknown zoned datetime trait " + value + ":" + safeClass(value));
    }

    public static OffsetDateTime asOffsetDateTime(Object value) {
        if (value instanceof OffsetDateTime) { return (OffsetDateTime) value; }
        else if (value instanceof ZonedDateTime) { return ((ZonedDateTime) value).toOffsetDateTime(); }
        else if (value instanceof LocalDateTime) { return ((LocalDateTime) value).atOffset(UTC); }
        else if (value instanceof String) { return parseDateTime((String) value).toOffsetDateTime(); }
        else if (value instanceof Long) { return OffsetDateTime.ofInstant(Instant.ofEpochMilli((Long) value), UTC); }
        else if (value instanceof Date) { return ((Date) value).toInstant().atOffset(UTC); }
        else throw new IllegalArgumentException("unknown offset datetime trait " + value + ":" + safeClass(value));
    }

    public static LocalDateTime asLocalDateTime(Object value) {
        if (value instanceof LocalDateTime) { return (LocalDateTime) value; }
        else if (value instanceof OffsetDateTime) { return ((OffsetDateTime) value).toLocalDateTime(); }
        else if (value instanceof ZonedDateTime) { return ((ZonedDateTime) value).toLocalDateTime(); }
        else if (value instanceof String) { return parseDateTime((String) value).toLocalDateTime(); }
        else if (value instanceof Long) { return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) value), UTC); }
        else if (value instanceof Date) { return ((Date) value).toInstant().atZone(UTC).toLocalDateTime(); }
        else throw new IllegalArgumentException("unknown local datetime trait " + value + ":" + safeClass(value));
    }

    public static Date asDate(Object value) {
        if (value instanceof Date) { return (Date) value; }
        else if (value instanceof LocalDateTime) { return new Date(((LocalDateTime) value).toInstant(UTC).toEpochMilli()); }
        else if (value instanceof OffsetDateTime) { return new Date(((OffsetDateTime) value).toInstant().toEpochMilli()); }
        else if (value instanceof ZonedDateTime) { return new Date(((ZonedDateTime) value).toInstant().toEpochMilli()); }
        else if (value instanceof Long) { return new Date((Long) value); }
        else if (value instanceof String) { return new Date(parseDateTime((String) value).toInstant().toEpochMilli()); }
        else throw new IllegalArgumentException("unknown date trait " + value + ":" + safeClass(value));
    }

    public static long asEpoch(Object value) {
        if (value instanceof Date) { return ((Date) value).getTime(); }
        else if (value instanceof LocalDateTime) { return ((LocalDateTime) value).toInstant(UTC).toEpochMilli(); }
        else if (value instanceof OffsetDateTime) { return ((OffsetDateTime) value).toInstant().toEpochMilli(); }
        else if (value instanceof ZonedDateTime) { return ((ZonedDateTime) value).toInstant().toEpochMilli(); }
        else if (value instanceof Long) { return new Date((Long) value).getTime(); }
        else if (value instanceof String) { return parseDateTime((String) value).toInstant().toEpochMilli(); }
        else throw new IllegalArgumentException("unknown date trait " + value + ":" + safeClass(value));
    }

    public static byte asByte(Object value) {
        if (value instanceof Byte) { return (Byte) value; }
        else if (value instanceof Number) { return ((Number) value).byteValue(); }
        else if (value instanceof String) { return Byte.parseByte((String) value); }
        else throw new IllegalArgumentException("unknown byte trait " + value + ":" + safeClass(value));
    }

    public static short asShort(Object value) {
        if (value instanceof Short) { return (Short) value; }
        else if (value instanceof Number) { return ((Number) value).shortValue(); }
        else if (value instanceof String) { return Short.parseShort((String) value); }
        else throw new IllegalArgumentException("unknown short trait " + value + ":" + safeClass(value));
    }

    public static int asInt(Object value) {
        if (value instanceof Integer) { return (Integer) value; }
        else if (value instanceof Number) { return ((Number) value).intValue(); }
        else if (value instanceof String) { return Integer.parseInt((String) value); }
        else throw new IllegalArgumentException("unknown int trait " + value + ":" + safeClass(value));
    }

    public static long asLong(Object value) {
        if (value instanceof Long) { return (Long) value; }
        else if (value instanceof BigInteger) { return ((BigInteger) value).longValueExact(); }
        else if (value instanceof Number) { return ((Number) value).longValue(); }
        else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException nfe) { return parseDateTime((String) value).toInstant().toEpochMilli(); }
        }
        else throw new IllegalArgumentException("unknown long trait " + value + ":" + safeClass(value));
    }

    public static float asFloat(Object value) {
        if (value instanceof Float) { return (Float) value; }
        else if (value instanceof Number) { return ((Number) value).floatValue(); }
        else if (value instanceof String) { return Float.parseFloat((String) value); }
        else throw new IllegalArgumentException("unknown float trait " + value + ":" + safeClass(value));
    }

    public static double asDouble(Object value) {
        if (value instanceof Double) { return (Double) value; }
        else if (value instanceof Number) { return ((Number) value).doubleValue(); }
        else if (value instanceof String) { return Double.parseDouble((String) value); }
        else throw new IllegalArgumentException("unknown double trait " + value + ":" + safeClass(value));
    }

    public static BigInteger asBigInt(Object value) { return (value instanceof BigInteger) ? (BigInteger) value : BigInteger.valueOf(asLong(value)); }
}