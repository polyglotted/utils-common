package io.polyglotted.common.test;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static io.polyglotted.common.util.BaseSerializer.deserialize;
import static io.polyglotted.common.util.BaseSerializer.serialize;
import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.ListBuilder.simpleList;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.MapBuilder.immutableMapBuilder;
import static io.polyglotted.common.util.MapBuilder.simpleMap;
import static io.polyglotted.common.util.MapBuilder.simpleMapBuilder;
import static io.polyglotted.common.util.MapRetriever.MAP_CLASS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class MapBuilderTest {
    public static Object[][] mapInputs() {
        return new Object[][]{
            {simpleMap()},
            {simpleMap("a", 1)},
            {simpleMap("a", 1, "b", true)},
            {simpleMap("a", 1, "b", true, "c", 2.0)},
            {simpleMap("a", 1, "b", true, "c", 2.0, "d", "2018-03-06")},
            {simpleMap("a", 1, "b", true, "c", 2.0, "d", "2018-03-06", "e", "x")},
            {simpleMap(immutableMap("a", 1))},
            {immutableMap()},
            {immutableMap("a", 1)},
            {immutableMap("a", 1, "b", true)},
            {immutableMap("a", 1, "b", true, "c", 2.0)},
            {immutableMap("a", 1, "b", true, "c", 2.0, "d", "2018-03-06")},
            {immutableMap("a", 1, "b", true, "c", 2.0, "d", "2018-03-06", "e", "x")},
            {immutableMap(simpleMap("a", 1))},
        };
    }

    @Test @Parameters(method = "mapInputs")
    public void mapBuilderAll(Map<String, Object> expected) {
        String json = serialize(expected);
        assertThat(json, deserialize(json, MAP_CLASS), is(expected));
    }

    public static Object[][] mapListInputs() {
        return new Object[][]{
            {simpleMapBuilder().putList("foo", simpleList()).build()},
            {simpleMapBuilder().putList("foo", simpleList(1, 2, 3)).build()},
            {immutableMapBuilder().putList("foo", immutableList()).build()},
            {immutableMapBuilder().putList("foo", immutableList(1, 2, 3)).build()},
        };
    }

    @Test @Parameters(method = "mapListInputs")
    public void mapBuilderListAll(Map<String, Object> expected) {
        String json = serialize(expected);
        assertThat(json, deserialize(json, MAP_CLASS), is(expected));
    }
}