package io.polyglotted.common.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Crypto {
    public static final String PASSWORD_SYSTEM_PROPERTY = "jasypt.encryptor.password";
    private static final String PREFIX = "ENC(";
    private static final String SUFFIX = ")";
    private final boolean supportEncryption;
    private final StringEncryptor encryptor;

    public Crypto() { this(resolvePassword()); }

    public Crypto(String password) { this(notNullOrEmpty(password), create(notNullOrEmpty(password) ? password : "abcde")); }

    public String encrypt(String toEncrypt) {
        return (supportEncryption && notNullOrEmpty(toEncrypt)) ? String.format("%s%s%s", PREFIX, encryptor.encrypt(toEncrypt), SUFFIX) : toEncrypt;
    }

    public String decrypt(String encrypted) {
        if (supportEncryption && notNullOrEmpty(encrypted) && encrypted.startsWith(PREFIX) && encrypted.endsWith(SUFFIX)) {
            encrypted = encrypted.substring(PREFIX.length(), encrypted.length() - SUFFIX.length());
            return encryptor.decrypt(encrypted);
        }
        return encrypted;
    }

    private static String resolvePassword() {
        String password = System.getProperty(PASSWORD_SYSTEM_PROPERTY);
        return notNullOrEmpty(password) ? password : System.getenv(PASSWORD_SYSTEM_PROPERTY);
    }

    private static StringEncryptor create(String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        return encryptor;
    }

    private static boolean notNullOrEmpty(String word) { return word != null && !"".equals(word); }
}