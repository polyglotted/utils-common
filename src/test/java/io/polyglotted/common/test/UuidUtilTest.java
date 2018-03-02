package io.polyglotted.common.test;

import io.polyglotted.common.util.UuidUtil;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UuidUtilTest extends UuidUtil {

    @Test
    public void testFromString() {
        for (int i = 0; i < 1000; i++) {
            UUID uuid = UUID.randomUUID();
            assertThat(uuidFrom(uuid.toString()), is(uuid));
        }
    }

    @Test
    public void testGenUuid() { assertThat(genUuidStr("from-me"), is("f57f7027-33c9-5173-a00f-8ae3cdd93ff4")); }

    @Test
    public void testToBytes() {
        for (int i = 0; i < 1000; i++) {
            UUID expected = UUID.randomUUID();
            byte[] bytesFromUuid = toBytes(expected);
            byte[] bytesFromString = toBytes(expected.toString());
            assertThat(bytesFromString, is(bytesFromUuid));
            assertThat(uuidFrom(bytesFromUuid), is(expected));
        }
    }
}