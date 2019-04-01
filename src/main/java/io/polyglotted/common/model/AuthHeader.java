package io.polyglotted.common.model;

import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import javax.annotation.Nullable;

import static io.polyglotted.common.util.StrUtil.nullOrEmpty;
import static io.polyglotted.common.util.StrUtil.safePrefix;
import static io.polyglotted.common.util.StrUtil.safeSuffix;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@SuppressWarnings("unused") @RequiredArgsConstructor
public class AuthHeader {
    @Nullable public final String authHeader;

    public String[] decipher() {
        String type = safePrefix(authHeader, " ");
        String[] decoded = new String(decodeBase64(safeSuffix(authHeader, " ")), UTF_8).split(":");
        return new String[]{type, decoded[0], decoded[1]};
    }

    public Header[] headers() { return nullOrEmpty(authHeader) ? new Header[0] : new Header[]{new BasicHeader(AUTHORIZATION, authHeader)}; }

    public static AuthHeader basicAuth(String user, String passwd) { return new AuthHeader("Basic " + b64Encode(user, passwd)); }

    public static AuthHeader customAuth(String type, String user, String passwd) { return new AuthHeader(type + " " + b64Encode(user, passwd)); }

    public static AuthHeader bearerToken(String token) { return new AuthHeader("Bearer " + token); }

    @SuppressWarnings("unused") public static AuthHeader authHeader(Object auth) { return new AuthHeader(auth == null ? null : String.valueOf(auth)); }

    private static String b64Encode(String user, String creds) { return encodeBase64String((user + ":" + creds).getBytes(UTF_8)); }
}