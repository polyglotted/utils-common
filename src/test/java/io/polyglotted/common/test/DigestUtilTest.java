package io.polyglotted.common.test;

import io.polyglotted.common.util.DigestUtil;
import org.junit.Test;

public class DigestUtilTest extends DigestUtil {

    @Test(expected = RuntimeException.class)
    public void testCreateMessageDigestFail() { createMessageDigest("abcd"); }
}