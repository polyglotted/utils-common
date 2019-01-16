package io.polyglotted.common.test;

import io.polyglotted.common.config.Crypto;
import org.junit.Test;

import static io.polyglotted.common.config.Crypto.PASSWORD_SYSTEM_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

public class CryptoTest {

    @Test
    public void shouldReturnRandomPassword() {
        Crypto cryptoClient = new Crypto();
        String encrypted = cryptoClient.encrypt("test");
        assertThat(encrypted, is("test"));
        String decrypted = cryptoClient.decrypt(encrypted);
        assertThat(decrypted, is("test"));
    }

    @Test
    public void shouldLookForPasswordFromSystemProperty() {
        System.setProperty(PASSWORD_SYSTEM_PROPERTY, "password");
        Crypto cryptoClient = new Crypto();
        String encrypted = cryptoClient.encrypt("test");
        assertThat(encrypted, is(not("test")));
        String decrypted = cryptoClient.decrypt(encrypted);
        assertThat(decrypted, is("test"));
        System.clearProperty(PASSWORD_SYSTEM_PROPERTY);
    }

    @Test
    public void shouldDecryptIfENCIndicatorPresent() {
        Crypto cryptoClient = new Crypto("password");
        String encrypted = cryptoClient.encrypt("test");
        String decrypted = cryptoClient.decrypt(encrypted);
        assertThat(decrypted, is("test"));
    }

    @Test
    public void shouldNotDecryptIfENCIndicatorNotPresent() {
        Crypto cryptoClient = new Crypto("password");
        String decrypted = cryptoClient.decrypt("test");
        assertThat(decrypted, is("test"));
    }

    @Test
    public void shouldNotAttemptToDecryptNullValue() {
        Crypto cryptoClient = new Crypto("password");
        String decrypted = cryptoClient.decrypt(null);
        assertThat(decrypted, is(nullValue()));
    }

    @Test
    public void shouldNotAttemptToDecryptEmptyValue() {
        Crypto cryptoClient = new Crypto("password");
        String decrypted = cryptoClient.decrypt("");
        assertThat(decrypted, is(""));
    }

    @Test
    public void shouldEncryptValueAndReturnENCFormatString() {
        Crypto cryptoClient = new Crypto("password");
        String encrypted = cryptoClient.encrypt("test");
        assertThat(encrypted, is(notNullValue()));
        assertThat(encrypted, startsWith("ENC("));
        assertThat(encrypted, endsWith(")"));
    }

    @Test
    public void shouldNotAttemptToEncryptNullValue() {
        Crypto cryptoClient = new Crypto("password");
        String encrypted = cryptoClient.encrypt(null);
        assertThat(encrypted, is(nullValue()));
    }

    @Test
    public void shouldNotAttemptToEncryptEmptyValue() {
        Crypto cryptoClient = new Crypto("password");
        String encrypted = cryptoClient.encrypt("");
        assertThat(encrypted, is(""));
    }
}