package io.polyglotted.common.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

import static io.polyglotted.common.util.BaseSerializer.serialize;
import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor @EqualsAndHashCode
public class AuthToken {
    public final String accessToken;
    public final Integer expiresIn;
    public final String tokenType;
    public final String refreshToken;

    /* ignore - for serialisation */
    private AuthToken() { this(null, null, null, null); }

    public String toJson() { return serialize(this); }

    public static AuthToken buildWith(Map<String, Object> map) { return io.polyglotted.common.model.Builder.buildWith(map, Builder.class); }

    public static AuthToken authToken(String token) { return tokenBuilder().accessToken(token).build(); }

    public static Builder tokenBuilder() { return new Builder(); }

    @Setter @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder implements io.polyglotted.common.model.Builder<AuthToken> {
        @Name("access_token") private String accessToken;
        @Name("expires_in") private Integer expiresIn = 0;
        @Name("type") private String tokenType = "Bearer";
        @Name("refresh_token") private String refreshToken;

        @Override public AuthToken build() { return new AuthToken(requireNonNull(accessToken, "accessToken"), expiresIn, tokenType, refreshToken); }
    }
}