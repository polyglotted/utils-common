package io.polyglotted.common.test;

import io.polyglotted.common.util.ResourceUtil;
import org.junit.Test;

import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ResourceUtilTest extends ResourceUtil {
    @Test
    public void readResourceAsMap() {
        assertThat(readResourceAsMap(ResourceUtilTest.class, "files/sample.txt"), is(immutableMap("hello", "world", "foo", "bar")));
    }

    @Test
    public void readResourceBytes() {
        assertThat(readResourceBytes(ResourceUtilTest.class, "files/sample.txt"), is("hello=world\nfoo=bar".getBytes()));
    }

    @Test
    public void readResource() {
        assertThat(readResource(ResourceUtilTest.class, "files/sample.txt"), is("hello=world\nfoo=bar"));
    }

    @Test
    public void readResourceList() {
        assertThat(readResourceList(ResourceUtilTest.class, "files/sample.txt"), is(immutableList("hello=world", "foo=bar")));
    }
}