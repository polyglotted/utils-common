package io.polyglotted.common.test;

import com.google.common.net.InetAddresses;
import io.polyglotted.common.model.GeoPoint;
import io.polyglotted.common.util.Sanitizer;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static io.polyglotted.common.util.UuidUtil.uuidFrom;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class SanitizerTest extends Sanitizer {
    @SneakyThrows public static Object[][] sanitizeInputs() {
        return new Object[][]{
            {Object.class, null, null},
            {TimeUnit.class, "SECONDS", TimeUnit.SECONDS},
            {Boolean.TYPE, "true", true},
            {Boolean.class, "true", true},
            {Byte.TYPE, "0", (byte) 0},
            {Byte.class, "0", (byte) 0},
            {Short.TYPE, "0", (short) 0},
            {Short.class, "0", (short) 0},
            {Integer.TYPE, "0", 0},
            {Integer.class, "0", 0},
            {Long.TYPE, "0", 0L},
            {Long.class, "0", 0L},
            {Float.TYPE, "0", 0f},
            {Float.class, "0", 0f},
            {Double.TYPE, "0", 0.0},
            {Double.class, "0", 0.0},
            {BigInteger.class, "0", BigInteger.ZERO},
            {byte[].class, "Zm9v", "foo".getBytes()},
            {ByteBuffer.class, "Zm9v", ByteBuffer.wrap("foo".getBytes())},
            {LocalDate.class, "2016-02-15", LocalDate.of(2016, 2, 15)},
            {LocalTime.class, "04:30", LocalTime.of(4, 30)},
            {OffsetTime.class, "04:30", LocalTime.of(4, 30).atOffset(UTC)},
            {OffsetTime.class, "04:30Z", LocalTime.of(4, 30).atOffset(UTC)},
            {ZonedDateTime.class, "2016-02-15 04:30", ZonedDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)},
            {LocalDateTime.class, "2016-02-15 04:30", LocalDateTime.of(2016, 2, 15, 4, 30, 0, 0)},
            {OffsetDateTime.class, "2016-02-15 04:30", OffsetDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)},
            {Date.class, "2016-02-15 04:30", new Date(1455510600000L)},
            {java.util.UUID.class, "f57f7027-33c9-5173-a00f-8ae3cdd93ff4", uuidFrom("f57f7027-33c9-5173-a00f-8ae3cdd93ff4")},
            {InetAddress.class, "172.0.0.1", InetAddresses.forString("172.0.0.1")},
            {Inet4Address.class, "172.0.0.1", InetAddresses.forString("172.0.0.1")},
            {GeoPoint.class, "[-90.0,90.0]", new GeoPoint(90, -90)},
            {URL.class, "http://google.com", new URL("http://google.com")},
            {URI.class, "s3://zing.com", new URI("s3://zing.com")},
            {Object.class, "foo-bar", "foo-bar"},
        };
    }

    @Test @Parameters(method = "sanitizeInputs")
    public void testSanitize(Class<?> clazz, Object input, Object expected) { assertThat(sanitize(clazz, input), is(expected)); }
}