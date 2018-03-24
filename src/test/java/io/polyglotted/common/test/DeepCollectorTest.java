package io.polyglotted.common.test;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.ListBuilder.simpleList;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.MapRetriever.deepCollect;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class DeepCollectorTest {
    public static Object[][] mapInputs() {
        return new Object[][]{
            {mapWithVal(), "value", String.class, immutableList("foo")},
            {mapWithArr(), "value", String.class, immutableList("foo", "bar")},
            {mapWithNullEmpties(), "value.key", String.class, immutableList("")},
            //{deserialize("{\"value\":{\"key\":[\"\"]}}"), "value.key", String.class, immutableList()},
            {mapWithMap(), "value", Map.class, immutableList(immutableMap("key", "foo"))},
            {mapWithMapArr(), "value", Map.class, immutableList(immutableMap("key", "foo"), immutableMap("key", "bar"))},
            {mapWithMap(), "value.key", String.class, immutableList("foo")},
            {mapWithMapArr(), "value.key", String.class, immutableList("foo", "bar")},
            {mapWithMulti(), "value.keys.key", String.class, immutableList("foo", "bar")},
        };
    }

    @Test @Parameters(method = "mapInputs")
    public <T> void deepCollectSuccess(Map<String, Object> map, String prop, Class<T> expClass, List<T> expected) {
        assertThat(deepCollect(map, prop, expClass), is(expected));
    }

    @Test public void deepCollectSingle() {
        Map<String, Object> src = immutableMap("identifiers", immutableMap("foo", "ali@foo.net", "bars", immutableList("kate@foo.net")));
        assertThat(deepCollect(src, "identifiers.foo", String.class), is(immutableList("ali@foo.net")));
        assertThat(deepCollect(src, "identifiers.bazs", String.class), is(immutableList()));
        assertThat(deepCollect(src, "identifiers.bars", String.class), is(immutableList("kate@foo.net")));
    }

    private static Map<String, Object> mapWithVal() { return immutableMap("value", "foo"); }

    private static Map<String, Object> mapWithArr() { return immutableMap("value", simpleList("foo", "bar")); }

    private static Map<String, Object> mapWithNullEmpties() { return immutableMap("value", immutableMap("key", simpleList(null, ""))); }

    private static Map<String, Object> mapWithMap() { return immutableMap("value", immutableMap("key", "foo")); }

    private static Map<String, Object> mapWithMapArr() {
        return immutableMap("value", immutableList(immutableMap("key", "foo"), immutableMap("key", "bar")));
    }

    private static Map<String, Object> mapWithMulti() {
        return immutableMap("value", immutableList(immutableMap("keys", immutableList(immutableMap("key", "foo"))),
            immutableMap("keys", immutableList(immutableMap("key", "bar")))));
    }
}