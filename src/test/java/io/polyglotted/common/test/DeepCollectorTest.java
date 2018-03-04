package io.polyglotted.common.test;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Lists.newArrayList;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.MapRetriever.deepCollect;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class DeepCollectorTest {
    public static Object[][] mapInputs() {
        return new Object[][]{
            {mapWithVal(), "value", String.class, of("foo")},
            {mapWithArr(), "value", String.class, of("foo", "bar")},
            {mapWithNullEmpties(), "value.key", String.class, of("")},
            //{deserialize("{\"value\":{\"key\":[\"\"]}}"), "value.key", String.class, of()},
            {mapWithMap(), "value", Map.class, of(immutableMap("key", "foo"))},
            {mapWithMapArr(), "value", Map.class, of(immutableMap("key", "foo"), immutableMap("key", "bar"))},
            {mapWithMap(), "value.key", String.class, of("foo")},
            {mapWithMapArr(), "value.key", String.class, of("foo", "bar")},
            {mapWithMulti(), "value.keys.key", String.class, of("foo", "bar")},
        };
    }

    @Test @Parameters(method = "mapInputs")
    public <T> void deepCollectSuccess(Map<String, Object> map, String prop, Class<T> expClass, List<T> expected) {
        assertThat(deepCollect(map, prop, expClass), is(expected));
    }

    @Test public void deepCollectSingle() {
        Map<String, Object> src = immutableMap("identifiers", immutableMap("foo", "ali@foo.net", "bars", of("kate@foo.net")));
        assertThat(deepCollect(src, "identifiers.foo", String.class), is(of("ali@foo.net")));
        assertThat(deepCollect(src, "identifiers.bazs", String.class), is(of()));
        assertThat(deepCollect(src, "identifiers.bars", String.class), is(of("kate@foo.net")));
    }

    private static Map<String, Object> mapWithVal() { return immutableMap("value", "foo"); }

    private static Map<String, Object> mapWithArr() { return immutableMap("value", newArrayList("foo", "bar")); }

    private static Map<String, Object> mapWithNullEmpties() { return immutableMap("value", immutableMap("key", newArrayList(null, ""))); }

    private static Map<String, Object> mapWithMap() { return immutableMap("value", immutableMap("key", "foo")); }

    private static Map<String, Object> mapWithMapArr() { return immutableMap("value", of(immutableMap("key", "foo"), immutableMap("key", "bar"))); }

    private static Map<String, Object> mapWithMulti() {
        return immutableMap("value", of(immutableMap("keys", of(immutableMap("key", "foo"))),
            immutableMap("keys", of(immutableMap("key", "bar")))));
    }
}