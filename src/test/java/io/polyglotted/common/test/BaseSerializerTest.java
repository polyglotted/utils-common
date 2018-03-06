package io.polyglotted.common.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.InetAddresses;
import io.polyglotted.common.model.GeoPoint;
import io.polyglotted.common.model.GeoShape;
import io.polyglotted.common.model.GeoType;
import io.polyglotted.common.model.MapResult;
import io.polyglotted.common.test.ObjectInputs.CollClass;
import io.polyglotted.common.test.ObjectInputs.RefClass;
import io.polyglotted.common.test.ObjectInputs.SimpleClass;
import io.polyglotted.common.test.ObjectInputs.Simplified;
import io.polyglotted.common.util.BaseSerializer;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import static io.polyglotted.common.test.ObjectInputs.MyConst.BAZ;
import static io.polyglotted.common.util.ObjConstructor.construct;
import static io.polyglotted.common.util.ReflectionUtil.create;
import static io.polyglotted.common.util.UuidUtil.uuidFrom;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class BaseSerializerTest extends BaseSerializer {

    public static Object[][] objInputs() throws Exception {
        return new Object[][]{
            {new SimpleClass().aString("foo").anIp(InetAddresses.forString("172.0.0.1")).aUrl(new URL("http://www.google.com"))
                .aUri(new URI("s3://foo.zing.com")).aUuid(uuidFrom("f57f7027-33c9-5173-a00f-8ae3cdd93ff4")).aBoolean(true)
                .aGeoPoint(new GeoPoint(-90, 90)).aGeoShape(new GeoShape(GeoType.point, "-1.1", null))
                .aBinary("foo".getBytes()).aBuffer(ByteBuffer.wrap("bar".getBytes())).aDateTime(ZonedDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC))
                .bDateTime(OffsetDateTime.of(2016, 2, 15, 4, 30, 0, 0, UTC)).bTime(LocalTime.of(4, 30).atOffset(UTC)).aDate(LocalDate.of(2016, 2, 15))
                .aTime(LocalTime.of(4, 30)).cDateTime(LocalDateTime.of(2016, 2, 15, 4, 30, 0, 0)).dDateTime(new Date(1455510600000L))
            },
            {new Simplified().fullStr("foo").email("foo@bar.co").bigInt(BigInteger.TEN).date(1455510600000L).prim("tux")},
            {new CollClass().booleanList(ImmutableList.of(true, false, true)).doubleList(ImmutableList.of(10.0, (double) 20, 30.0))
                .longSet(ImmutableSet.of(5L, 2L)).dateSet(ImmutableSet.of(new Date(1455510600000L), new Date(1455510700000L)))
                .localDates(ImmutableList.of(LocalDate.of(2016, 2, 15))).objectSet(ImmutableSet.of("foo", true, 2))
                .stringIntegerMap(ImmutableMap.of("a", 1, "b", 2)).primMap(ImmutableMap.of("a", "foo", "b", 2, "c", false))
            },
            {new RefClass().simplified(new Simplified().email("b@c.io")).simples(ImmutableList.of(new SimpleClass().aString("oui"),
                new SimpleClass().aDate(LocalDate.of(2017, 1, 25)))).schemeMap(ImmutableMap.of(BAZ, new SimpleClass().anInt(25)))
            },
        };
    }

    @Test @Parameters(method = "objInputs")
    public void serializeNative(Object expected) throws Exception {
        String json = serialize(expected);
        Object actual = deserialize(json, expected.getClass());
        assertThat(serialize(actual), actual, is(expected));

        MapResult mapResult = deserialize(json);
        assertThat(serialize(mapResult), json, is(serialize(mapResult)));
    }

    @Test @Parameters(method = "objInputs")
    public void serializeAndConstruct(Object expected) throws Exception {
        byte[] bytes = serializeBytes(expected);
        Object actual = construct(deserialize(bytes), create(expected.getClass()));
        assertThat(serialize(actual), actual, is(expected));

        MapResult mapResult = deserialize(bytes);
        assertThat(serialize(mapResult), bytes, is(serializeBytes(mapResult)));
    }

    public static Object[][] jsonInputs() throws Exception {
        return new Object[][]{
            {"{\"fullStr\":\"foo\",\"date\":\"2016-02-15T04:30Z\"}", new Simplified().fullStr("foo").date(1455510600000L)},
            {"{\"dateLongs\":[\"2016-02-15T04:30Z\"]}", new CollClass().dateLongs(ImmutableList.of(1455510600000L))},
            {"{\"primMap\":{\"qux\":\"2016-02-15T04:30Z\"}}", new CollClass().primMap(ImmutableMap.of("qux", "2016-02-15T04:30Z"))},
        };
    }

    @Test @Parameters(method = "jsonInputs")
    public void serializeStrAsDateLong(String json, Object expected) throws Exception {
        Object actual = deserialize(json, expected.getClass());
        assertThat(json, actual, is(expected));
        Object actual2 = construct(deserialize(json), create(expected.getClass()));
        assertThat(json, actual2, is(expected));
    }
}