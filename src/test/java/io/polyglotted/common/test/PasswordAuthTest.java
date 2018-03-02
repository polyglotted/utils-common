package io.polyglotted.common.test;

import io.polyglotted.common.util.PasswordAuth;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PasswordAuthTest extends PasswordAuth {
    @Test
    public void testAuthenticate() {
        char[] password = "mySecur3P@assw0rd".toCharArray();
        String hashPassword = hashPassword(password);
        assertThat(authenticate(password, hashPassword), is(true));
    }
}