package io.polyglotted.common.util;

import io.polyglotted.common.model.GeoPoint;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static io.polyglotted.common.util.EnumCache.fetchEnumValueFor;
import static io.polyglotted.common.util.ReflectionUtil.isEnum;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Sanitizer {
    private static final Map<Class<?>, Function<Object, Object>> FUNCTIONS_MAP = MapBuilder.<Class<?>, Function<Object, Object>>immutableMapBuilder()
        .put(Boolean.class, ConversionUtil::asBool)
        .put(Boolean.TYPE, ConversionUtil::asBool)
        .put(Byte.class, ConversionUtil::asByte)
        .put(Byte.TYPE, ConversionUtil::asByte)
        .put(Short.class, ConversionUtil::asShort)
        .put(Short.TYPE, ConversionUtil::asShort)
        .put(Integer.class, ConversionUtil::asInt)
        .put(Integer.TYPE, ConversionUtil::asInt)
        .put(Long.class, ConversionUtil::asLong)
        .put(Long.TYPE, ConversionUtil::asLong)
        .put(Float.class, ConversionUtil::asFloat)
        .put(Float.TYPE, ConversionUtil::asFloat)
        .put(Double.class, ConversionUtil::asDouble)
        .put(Double.TYPE, ConversionUtil::asDouble)
        .put(BigInteger.class, ConversionUtil::asBigInt)
        .put(byte[].class, ConversionUtil::asBinary)
        .put(ByteBuffer.class, ConversionUtil::asBuffer)
        .put(LocalDate.class, ConversionUtil::asLocalDate)
        .put(LocalTime.class, ConversionUtil::asLocalTime)
        .put(OffsetTime.class, ConversionUtil::asOffsetTime)
        .put(ZonedDateTime.class, ConversionUtil::asZonedDateTime)
        .put(OffsetDateTime.class, ConversionUtil::asOffsetDateTime)
        .put(LocalDateTime.class, ConversionUtil::asLocalDateTime)
        .put(Date.class, ConversionUtil::asDate)
        .put(UUID.class, ConversionUtil::asUuid)
        .put(InetAddress.class, ConversionUtil::asInetAddress)
        .put(Inet4Address.class, ConversionUtil::asInetAddress)
        .put(GeoPoint.class, ConversionUtil::asGeoPoint)
        .put(URL.class, ConversionUtil::asUrl)
        .put(URI.class, ConversionUtil::asUri)
        .build();

    public static boolean isSanitizable(Class<?> expectedType) { return isEnum(expectedType) || FUNCTIONS_MAP.containsKey(expectedType); }

    public static boolean isBinary(Object object) { return object instanceof byte[] || object instanceof ByteBuffer; }

    public static Object sanitize(Class<?> expectedType, Object value) {
        if (value == null) return null;
        else if (isEnum(expectedType)) { return fetchEnumValueFor(expectedType, String.valueOf(value)); }
        else if (FUNCTIONS_MAP.containsKey(expectedType)) { return FUNCTIONS_MAP.get(expectedType).apply(value); }
        else return value;
    }
}