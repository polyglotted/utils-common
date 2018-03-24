package io.polyglotted.common.test;

import io.polyglotted.common.util.CommaUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class CommaUtilTest extends CommaUtil {

    public static Object[][] commaInputs() {
        return new Object[][]{
            {null},
            {immutableList()},
            {immutableList("foo")},
            {immutableList("foo", "bar", "baz")},
        };
    }

    @Test @Parameters(method = "commaInputs")
    public void commaSplitJoinSuccess(List<String> values) {
        String joined = commaJoin(values);
        assertThat(commaSplit(joined), is(values));
    }

    @Test
    public void splitEmpty() { assertThat(commaSplit(""), is(immutableList())); }

    @Test
    public void mapSplitSuccess() { assertThat(mapSplit("foo=bar, tux=qux", "="), is(immutableMap("foo", "bar", "tux", "qux"))); }
}