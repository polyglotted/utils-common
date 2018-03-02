package io.polyglotted.common.test;

import io.polyglotted.common.util.NullUtil;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NullUtilTest extends NullUtil {

    @Test
    public void nonNullSuccess() {
        assertThat(nonNull(null, "a"), is("a"));
        assertThat(nonNull(null, null, "a"), is("a"));
        assertThat(nonNullIn(null, null, null, "a"), is("a"));
    }
}