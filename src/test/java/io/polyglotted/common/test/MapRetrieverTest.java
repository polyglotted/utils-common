package io.polyglotted.common.test;

import com.google.common.collect.ImmutableMap;
import io.polyglotted.common.util.MapRetriever;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.MapBuilder.simpleMap;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class MapRetrieverTest extends MapRetriever {

    public static Object[][] longStrValInputs() {
        return new Object[][]{
            {immutableMap(), -1L},
            {simpleMap("longStr", null), -1L},
            {simpleMap("longStr", 25), 25L},
            {simpleMap("longStr", 18286378909L), 18286378909L},
            {simpleMap("longStr", BigInteger.TEN), 10L},
            {simpleMap("longStr", "25"), 25L},
            {simpleMap("longStr", "18286378909"), 18286378909L},
        };
    }

    @Test @Parameters(method = "longStrValInputs")
    public void testLongStrVal(Map<String, Object> map, long expected) {
        assertThat(longStrVal(map, "longStr", -1L), is(expected));
    }

    @Test(expected = NumberFormatException.class)
    public void testLongStrValFail() { longStrVal(simpleMap("longStr", true), "longStr", -1L); }

    public static Object[][] mapInputs() {
        return new Object[][]{
            {stringDoubleMap(), "z", 1.2},
            {simple(101, "bob"), "id", 101},
            {variable("tplMap", nativeStringInnerMap()), "value.a3.name", "dave"},
            {varList(), "[1]", 25},
            {varMap(), "xv", true},
            {varMap(), "s2.name", "bill"},
            {varMap(), "list.[0]", "hello"},
            {varMap(), "list.[2].name", "dave"},
            {varMap(), "notFound", null},
            {varMap(), "y.z", null},
            {variable("tplMap", nativeStringInnerMap()), "value.notFound.item", null},
        };
    }

    @Test @Parameters(method = "mapInputs")
    public void testRetrieve(Object map, String prop, Object expected) { assertThat(deepRetrieve(map, prop), is(expected)); }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveIllegalProperty() { deepRetrieve(varMap(), ".value"); }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveIllegalList() { deepRetrieve(varList(), "value"); }

    @Test
    public void patternMatchTest() {
        assertThat(LIST_PATTERN.matcher("xy2").matches(), is(false));
        assertThat(LIST_PATTERN.matcher("[]").matches(), is(false));
        assertThat(LIST_PATTERN.matcher("[-2]").matches(), is(false));
        assertThat(LIST_PATTERN.matcher("[2.3]").matches(), is(false));
        assertThat(LIST_PATTERN.matcher("[a]").matches(), is(false));
        assertThat(LIST_PATTERN.matcher("[2a]").matches(), is(false));
        assertThat(LIST_PATTERN.matcher("[2]").matches(), is(true));
        assertThat(LIST_PATTERN.matcher("[23456]").matches(), is(true));
    }

    private static Map<String, Object> varMap() {
        return immutableMap("z", 10, "y", 2.4, "xv", true, "s2", simple(102, "bill"), "list", varList());
    }

    private static List<Object> varList() { return immutableList("hello", 25, simple(103, "dave")); }

    private static ImmutableMap<String, Double> stringDoubleMap() { return immutableMap("z", 1.2, "y", 2.4); }

    private static ImmutableMap<String, SimpleClass> nativeStringInnerMap() { return immutableMap("a3", simple(103, "dave")); }

    private static SimpleClass simple(int id, String name) { return new SimpleClass(id, name); }

    private static VariableClass variable(String key, Object value) { return new VariableClass(key, value); }

    @ToString
    @RequiredArgsConstructor
    @EqualsAndHashCode(doNotUseGetters = true)
    static class SimpleClass {
        public final int id;
        public final String name;
    }

    @ToString
    @RequiredArgsConstructor
    @EqualsAndHashCode(doNotUseGetters = true)
    static final class VariableClass {
        public final String key;
        public final Object value;
    }
}