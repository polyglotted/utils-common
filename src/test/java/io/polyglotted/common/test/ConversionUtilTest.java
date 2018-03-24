package io.polyglotted.common.test;

import io.polyglotted.common.util.ConversionUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import static io.polyglotted.common.util.ReflectionUtil.safeInvoke;
import static io.polyglotted.common.util.UuidUtil.uuidFrom;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class ConversionUtilTest extends ConversionUtil {
    public static Object[][] convFailInputs() {
        return new Object[][]{
            {"asBool", 0},
            {"asUuid", 0},
            {"asInetAddress", 0},
            {"asLocalDate", 0},
            {"asLocalTime", Boolean.FALSE},
            {"asOffsetTime", Boolean.FALSE},
            {"asZonedDateTime", Boolean.FALSE},
            {"asOffsetDateTime", Boolean.FALSE},
            {"asLocalDateTime", Boolean.FALSE},
            {"asDate", Boolean.FALSE},
            {"asByte", Boolean.FALSE},
            {"asShort", Boolean.FALSE},
            {"asInt", Boolean.FALSE},
            {"asLong", Boolean.FALSE},
            {"asFloat", Boolean.FALSE},
            {"asDouble", Boolean.FALSE},
            {"asBigInt", Boolean.FALSE},
        };
    }

    @Test(expected = IllegalArgumentException.class) @Parameters(method = "convFailInputs")
    public void testConversionFailures(String method, Object input) {
        safeInvoke(ConversionUtil.class, (Object) null, method, new Class<?>[]{Object.class}, input);
    }

    @Test
    public void testAsBool() {
        assertThat(asBool(true), is(true));
        assertThat(asBool("true"), is(true));
    }

    @Test
    public void testAsUuid() {
        UUID uuid = uuidFrom("f57f7027-33c9-5173-a00f-8ae3cdd93ff4");
        assertThat(asUuid(uuid), is(uuid));
        assertThat(asUuid("f57f7027-33c9-5173-a00f-8ae3cdd93ff4"), is(uuid));
    }

    @Test
    public void testAsInetAddress() {
        InetAddress result = InetAddress.getLoopbackAddress();
        assertThat(asInetAddress(result), is(result));
        assertThat(asInetAddress("127.0.0.1"), is(result));
    }

    @Test
    public void testAsLocalDate() {
        LocalDate result = LocalDate.of(2016, 2, 15);
        assertThat(asLocalDate(result), is(result));
        assertThat(asLocalDate(LocalDateTime.of(2016, 2, 15, 4, 30)), is(result));
        assertThat(asLocalDate(new Date(result.atStartOfDay().toInstant(UTC).toEpochMilli())), is(result));
        assertThat(asLocalDate("2016-02-15"), is(result));
    }

    @Test
    public void testAsLocalTime() {
        LocalTime result = LocalTime.of(4, 30);
        assertThat(asLocalTime(result), is(result));
        assertThat(asLocalTime(result.atOffset(UTC)), is(result));
        assertThat(asLocalTime(result.toSecondOfDay()), is(result));
        assertThat(asLocalTime(result.toNanoOfDay()), is(result));
        assertThat(asLocalTime("04:30"), is(result));
    }

    @Test
    public void testAsOffsetTime() {
        OffsetTime result = LocalTime.of(4, 30).atOffset(UTC);
        assertThat(asOffsetTime(result), is(result));
        assertThat(asOffsetTime(result.toLocalTime()), is(result));
        assertThat(asOffsetTime("04:30"), is(result));
    }

    @Test
    public void testAsZonedDateTime() {
        ZonedDateTime result = ZonedDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC);
        assertThat(asZonedDateTime(result), is(result));
        assertThat(asZonedDateTime(LocalDateTime.of(2016, 2, 15, 4, 30, 0, 0)), is(result));
        assertThat(asZonedDateTime(OffsetDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asZonedDateTime("2016-02-15 04:30"), is(result));
        assertThat(asZonedDateTime(1455510600000L), is(result));
        assertThat(asZonedDateTime(new Date(1455510600000L)), is(result));
    }

    @Test
    public void testAsZonedDateTimeString() {
        String result = "2016-02-15T04:30:00.000000Z";
        assertThat(asZonedDateTimeString(ZonedDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asZonedDateTimeString(LocalDateTime.of(2016, 2, 15, 4, 30, 0, 0)), is(result));
        assertThat(asZonedDateTimeString(OffsetDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asZonedDateTimeString("2016-02-15 04:30"), is(result));
        assertThat(asZonedDateTimeString(1455510600000L), is(result));
        assertThat(asZonedDateTimeString(new Date(1455510600000L)), is(result));
    }

    @Test
    public void testAsOffsetDateTime() {
        OffsetDateTime result = OffsetDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC);
        assertThat(asOffsetDateTime(result), is(result));
        assertThat(asOffsetDateTime(LocalDateTime.of(2016, 2, 15, 4, 30, 0, 0)), is(result));
        assertThat(asOffsetDateTime(ZonedDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asOffsetDateTime("2016-02-15 04:30"), is(result));
        assertThat(asOffsetDateTime(1455510600000L), is(result));
        assertThat(asOffsetDateTime(new Date(1455510600000L)), is(result));
    }

    @Test
    public void testAsLocalDateTime() {
        LocalDateTime result = LocalDateTime.of(2016, 2, 15, 4, 30, 0, 0);
        assertThat(asLocalDateTime(result), is(result));
        assertThat(asLocalDateTime(ZonedDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asLocalDateTime(OffsetDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asLocalDateTime("2016-02-15 04:30"), is(result));
        assertThat(asLocalDateTime(1455510600000L), is(result));
        assertThat(asLocalDateTime(new Date(1455510600000L)), is(result));
    }

    @Test
    public void testAsDate() {
        Date result = new Date(1455510600000L);
        assertThat(asDate(result), is(result));
        assertThat(asDate(LocalDateTime.of(2016, 2, 15, 4, 30, 0, 0)), is(result));
        assertThat(asDate(ZonedDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asDate(OffsetDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asDate("2016-02-15 04:30"), is(result));
        assertThat(asDate(1455510600000L), is(result));
    }

    @Test
    public void testAsEpoch() {
        long result = 1455510600000L;
        assertThat(asEpoch(new Date(1455510600000L)), is(result));
        assertThat(asEpoch(LocalDateTime.of(2016, 2, 15, 4, 30, 0, 0)), is(result));
        assertThat(asEpoch(ZonedDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asEpoch(OffsetDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)), is(result));
        assertThat(asEpoch("2016-02-15 04:30"), is(result));
        assertThat(asEpoch(1455510600000L), is(result));
    }

    @Test
    public void testAsByte() {
        assertThat(asByte((byte) 0), is((byte) 0));
        assertThat(asByte(0.0), is((byte) 0));
        assertThat(asByte("0"), is((byte) 0));
    }

    @Test
    public void testAsShort() {
        assertThat(asShort((short) 0), is((short) 0));
        assertThat(asShort(0.0), is((short) 0));
        assertThat(asShort("0"), is((short) 0));
    }

    @Test
    public void testAsInt() {
        assertThat(asInt(0), is(0));
        assertThat(asInt(0.0), is(0));
        assertThat(asInt("0"), is(0));
    }

    @Test
    public void testAsLong() {
        assertThat(asLong(0L), is(0L));
        assertThat(asLong(0.0), is(0L));
        assertThat(asLong(BigInteger.ZERO), is(0L));
        assertThat(asLong("0"), is(0L));
    }

    @Test
    public void testAsFloat() {
        assertThat(asFloat(2.2f), is(2.2f));
        assertThat(asFloat(2.2), is(2.2f));
        assertThat(asFloat("2.2"), is(2.2f));
    }

    @Test
    public void testAsDouble() {
        assertThat(asDouble(2), is(2.0));
        assertThat(asDouble(2.2), is(2.2));
        assertThat(asDouble("2.2"), is(2.2));
    }

    @Test
    public void testAsBigInt() {
        assertThat(asBigInt(BigInteger.ZERO), is(BigInteger.ZERO));
        assertThat(asBigInt(0), is(BigInteger.ZERO));
        assertThat(asBigInt("0"), is(BigInteger.ZERO));
    }
}