package io.polyglotted.common.test;

import io.polyglotted.common.util.StrUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static io.polyglotted.common.util.ListBuilder.immutableList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class StrUtilTest extends StrUtil {

    private static final String SINGLE_RESULT = "{\"&model\":\"Currency\",\"&id\":\"GBP\",\"&timestamp\":1482889641232,\"&result\":\"created\"}";

    public static Object[][] prefixInputs() {
        return new Object[][]{
            {"", "/", ""},
            {"hello", ":", ""},
            {"/hello", "/", ""},
            {"h/", "/", "h"},
            {"hello/", "/", "hello"},
            {"hello/world", "/", "hello"},
        };
    }

    @Test @Parameters(method = "prefixInputs")
    public void testSafePrefix(String word, String delim, String expected) { assertThat(safePrefix(word, delim), is(expected)); }

    @Test public void testSafePrefixWithDef() { assertThat(safePrefix("hello", "/", "hello"), is("hello")); }

    public static Object[][] suffixInputs() {
        return new Object[][]{
            {"", "/", ""},
            {"hello", "/", "hello"},
            {"/hello", "/", "hello"},
            {"h/", "/", ""},
            {"hello/w", "/", "w"},
            {"hello/world", "/", "world"},
        };
    }

    @Test @Parameters(method = "suffixInputs")
    public void testSafeSuffix(String word, String delim, String expected) { assertThat(safeSuffix(word, delim), is(expected)); }

    @Test
    public void testChangeAction() {
        assertThat(safePrefix(safeSuffix(SINGLE_RESULT, "&result\":\""), "\""), is("created"));
    }

    public static Object[][] lastSuffixInputs() {
        return new Object[][]{
            {"", "/", ""},
            {"hello", "/", ""},//change from suffix
            {"/hello", "/", "hello"},
            {"h/", "/", ""},
            {"hello/w", "/", "w"},
            {"hello/world", "/", "world"},
        };
    }

    @Test @Parameters(method = "lastSuffixInputs")
    public void testSafeLastSuffix(String word, String delim, String expected) { assertThat(safeLastSuffix(word, delim), is(expected)); }

    public static Object[][] stringOfInputs() {
        return new Object[][]{
            {immutableList(), ""},
            {immutableList("a"), "a"},
            {immutableList("a", "b"), "a, b"},
        };
    }

    @Test @Parameters(method = "stringOfInputs")
    public void testStringOf(List<String> strings, String expected) { assertThat(stringOf(strings), is(expected)); }
}