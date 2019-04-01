package io.polyglotted.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.polyglotted.common.util.ListBuilder.ImmutableListBuilder;
import io.polyglotted.common.util.MapBuilder;
import io.polyglotted.common.util.MapBuilder.ImmutableMapBuilder;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static io.polyglotted.common.model.MapResult.immutableResult;
import static io.polyglotted.common.model.MapResult.immutableResultBuilder;
import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.ListBuilder.immutableListBuilder;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.MapBuilder.simpleMapBuilder;
import static io.polyglotted.common.util.NullUtil.nonNull;
import static io.polyglotted.common.util.UrnUtil.safeUrnOf;
import static java.util.Objects.requireNonNull;
import static javax.management.timer.Timer.ONE_HOUR;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@SuppressWarnings({"unused", "WeakerAccess"})
@JsonPropertyOrder({"_meta", "principal", "attributes", "roles"})
@EqualsAndHashCode(of = {"principal", "attributes", "roles"})
@ToString(doNotUseGetters = true)
@Accessors(fluent = true) @RequiredArgsConstructor
public final class Subject {
    public final String principal;
    public final MapResult attributes;
    public final List<String> roles;
    @Getter @JsonInclude(NON_EMPTY) private final Map<String, Object> _meta;

    /* ignore - for serialisation */
    private Subject() { this(null, immutableResult(), immutableList(), immutableMap()); }

    public <T> T attribute(String key) { return attributes.optValue(key); }

    public String id() { return principal; }

    public String realm() { return attribute("REALM"); }

    public String userId() { return attribute("USER_ID"); }

    public String credential() { return attribute("CREDENTIAL"); }

    public String token() { return attribute("ACCESS_TOKEN"); }

    public String proxyUser() { return attribute("PROXY_USER"); }

    public String email() { return attribute("EMAIL"); }

    public String remoteAddr() { return nonNull(attribute("REMOTE_ADDR"), "10.0.0.1"); }

    public boolean isPartial() { return Boolean.TRUE.equals(attribute("PARTIAL_AUTH")); }

    public String authScheme() { return nonNull(attribute("AUTH_SCHEME"), "Basic"); }

    public String user() { return nonNull(attribute("DISPLAY_NAME"), principal); }

    public boolean isTrusted() { return Boolean.TRUE.equals(attribute("TRUSTED")); }

    public long userTtl() { return nonNull(attribute("USER_TTL"), ONE_HOUR); }

    public static Subject buildWith(Map<String, Object> map) { return io.polyglotted.common.model.Builder.buildWith(map, Builder.class); }

    public static Builder subjectBuilder() { return new Builder(); }

    @Setter @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder implements io.polyglotted.common.model.Builder<Subject> {
        private String principal;
        private final ImmutableMapBuilder<String, Object> attributes = immutableResultBuilder();
        private final ImmutableListBuilder<String> roles = immutableListBuilder();
        private final MapBuilder.SimpleMapBuilder<String, Object> _meta = simpleMapBuilder(TreeMap::new);

        public Builder usernameMd5(Object... parts) { return principal(md5Hex(safeUrnOf(parts))); }

        public Builder role(String role) { this.roles.add(role); return this; }

        public Builder roles(Iterable<String> roles) { this.roles.addAll(roles); return this; }

        public Builder attribute(String key, Object value) { this.attributes.put(key, value); return this; }

        public Builder attributes(Map<String, Object> meta) { this.attributes.putAll(meta); return this; }

        public Builder _meta(String name, String value) { _meta.put(name, value); return this; }

        public Builder _meta(Map<String, Object> map) { _meta.putAll(map); return this; }

        @Override public Subject build() {
            return new Subject(requireNonNull(principal, "principal"), attributes.immutableResult(), roles.build(), _meta.build());
        }
    }
}