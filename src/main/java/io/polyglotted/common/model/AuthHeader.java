package io.polyglotted.common.model;

import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import javax.annotation.Nullable;

import static io.polyglotted.common.util.StrUtil.nullOrEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@SuppressWarnings("unused") @RequiredArgsConstructor
public class AuthHeader {
    @Nullable public final String authHeader;

    public Header[] headers() { return nullOrEmpty(authHeader) ? new Header[0] : new Header[]{new BasicHeader(AUTHORIZATION, authHeader)}; }

    public static AuthHeader basicAuth(String user, String passwd) { return new AuthHeader(AuthType.BASIC.header(user, passwd)); }

    public static AuthHeader bearerToken(String token) { return new AuthHeader(AuthType.BEARER.header(null, token)); }

    @SuppressWarnings("unused") public static AuthHeader authHeader(Object auth) { return new AuthHeader(auth == null ? null : String.valueOf(auth)); }

    public enum AuthType {
        BASIC {
            @Override String header(String user, String creds) { return "Basic " + encodeBase64String((user + ":" + creds).getBytes(UTF_8)); }
        },
        BEARER {
            @Override String header(String user, String token) { return "Bearer " + token; }
        };

        abstract String header(String user, String creds);
    }
}