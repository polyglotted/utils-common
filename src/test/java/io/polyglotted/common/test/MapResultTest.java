package io.polyglotted.common.test;

import io.polyglotted.common.model.MapResult;
import io.polyglotted.common.model.MapResult.ImmutableResult;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static io.polyglotted.common.model.MapResult.immutableResult;
import static io.polyglotted.common.model.MapResult.simpleResult;
import static io.polyglotted.common.model.SortedMapResult.treeResult;
import static io.polyglotted.common.util.BaseSerializer.serialize;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.MapBuilder.simpleMap;
import static java.time.LocalDate.now;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class MapResultTest {
    public static Object[][] resultInputs() {
        return new Object[][]{
            {simpleResult()},
            {simpleResult("a", 1)},
            {simpleResult("a", 1, "b", true)},
            {simpleResult("a", 1, "b", true, "c", 2.0)},
            {simpleResult("a", 1, "b", true, "c", 2.0, "d", now())},
            {simpleResult("a", 1, "b", true, "c", 2.0, "d", now(), "e", "x")},
            {simpleResult(simpleMap("a", 1))},
            {treeResult()},
            {treeResult("a", 1)},
            {treeResult("a", 1, "b", true)},
            {treeResult("a", 1, "b", true, "c", 2.0)},
            {treeResult("a", 1, "b", true, "c", 2.0, "d", now())},
            {treeResult("a", 1, "b", true, "c", 2.0, "d", now(), "e", "x")},
            {treeResult(simpleMap("a", 1))},
            {immutableResult()},
            {immutableResult("a", 1)},
            {immutableResult("a", 1, "b", true)},
            {immutableResult("a", 1, "b", true, "c", 2.0)},
            {immutableResult("a", 1, "b", true, "c", 2.0, "d", now())},
            {immutableResult("a", 1, "b", true, "c", 2.0, "d", now(), "e", "x")},
            {immutableResult(immutableMap("a", 1))},
        };
    }

    @Test @Parameters(method = "resultInputs")
    public void mapResultAll(ImmutableResult expected) {
        assertThat(inAndOut(expected), is(expected));
        assertThat(outOnly(expected), is(expected));
        assertThat(inOnly(expected), is(expected));
        assertThat(expected.immutable(), is(immutableResult(expected)));
        assertThat(serialize(expected), is(notNullValue()));
    }

    @Test
    public void mapResultEquals() {
        assertThat(immutableResult("a", "b", "c", 1), is(simpleResult("a", "b", "c", 1)));
    }

    private static Map<String, Object> inAndOut(Map<String, Object> map) { return map; }

    private static Map<String, Object> outOnly(MapResult map) { return map; }

    private static MapResult inOnly(Map<String, Object> map) { return (MapResult) map; }
}