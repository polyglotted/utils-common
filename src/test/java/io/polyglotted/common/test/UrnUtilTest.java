package io.polyglotted.common.test;

import io.polyglotted.common.model.Pair;
import io.polyglotted.common.util.UrnUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class UrnUtilTest extends UrnUtil {

    public static Object[][] urnInputs() {
        return new Object[][]{
            {"foo:bar", "foo", "bar"},
            {"foo:*", "foo", "*"},
            {"*", "", "*"},
            {":*", "", "*"},
            {"", "", ""},
        };
    }

    @Test @Parameters(method = "urnInputs")
    public void urnSplitSuccess(String urn, String prefix, String suffix) {
        Pair<String, String> split = urnSplit(urn);
        assertThat(split._a, is(prefix));
        assertThat(split._b, is(suffix));
        assertThat(first(urn), is(prefix));
    }

    public static Object[][] urnOfInputs() {
        return new Object[][]{
            {null, null, ""},
            {null, "", ""},
            {"", null, ""},
            {"", "", ""},
            {"a", "", "a"},
            {"", "b", "b"},
            {"a", "b", "a:b"},
        };
    }

    @Test @Parameters(method = "urnOfInputs")
    public void urnOfSuccess(String a, String b, String urn) { assertThat(urnOf(a, b), is(urn)); }
}