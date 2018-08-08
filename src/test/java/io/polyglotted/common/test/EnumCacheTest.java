package io.polyglotted.common.test;

import io.polyglotted.common.util.EnumCache;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.Test;

import static io.polyglotted.common.util.ReflectionUtil.asEnum;
import static io.polyglotted.common.util.ReflectionUtil.isEnum;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class EnumCacheTest extends EnumCache {
    @Test
    public void testFetchEnumFor() {
        assertThat(isEnum(MyEnum.class), is(true));
        assertThat(asEnum(MyEnum.class, "foo"), is(MyEnum.FOO));
        assertThat(fetchEnumFor(MyEnum.class, "foo"), is(MyEnum.FOO));
        assertThat(fetchEnumFor(MyEnum.class, "FOO"), is(MyEnum.FOO));
        assertThat(fetchEnumFor(MyEnum.class, "Foo"), is(nullValue()));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum MyEnum {
        FOO("foo");
        private final String str;

        @Override public String toString() { return str; }
    }
}