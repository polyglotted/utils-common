package io.polyglotted.common.test;

import com.google.common.collect.ImmutableMap;
import io.polyglotted.common.util.CommaUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class CommaUtilTest extends CommaUtil {

    public static Object[][] commaInputs() {
        return new Object[][]{
            {null},
            {of()},
            {of("foo")},
            {of("foo", "bar", "baz")},
        };
    }

    @Test @Parameters(method = "commaInputs")
    public void commaSplitJoinSuccess(List<String> values) {
        String joined = commaJoin(values);
        assertThat(commaSplit(joined), is(values));
    }

    @Test
    public void splitEmpty() { assertThat(commaSplit(""), is(of())); }

    @Test
    public void mapSplitSuccess() { assertThat(mapSplit("foo=bar, tux=qux", "="), is(ImmutableMap.of("foo", "bar", "tux", "qux"))); }
}