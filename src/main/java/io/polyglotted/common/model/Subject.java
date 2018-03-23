package io.polyglotted.common.model;

import com.google.common.collect.Iterables;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.polyglotted.common.model.MapResult.immutableResult;
import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.UrnUtil.safeUrnOf;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@SuppressWarnings("unused")
@RequiredArgsConstructor @EqualsAndHashCode @ToString
public final class Subject {
    public final String username;
    public final List<String> roles;
    public final MapResult metadata;
    public final boolean enabled;
    @Nullable public final String fullName;
    @Nullable public final String email;

    /* ignore - for serialisation */
    private Subject() { this(null, immutableList(), immutableResult(), true, null, null); }

    public static Subject buildWith(Map<String, Object> map) { return io.polyglotted.common.model.Builder.buildWith(map, Builder.class); }

    public static Builder subjectBuilder() { return new Builder(); }

    @Setter @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder implements io.polyglotted.common.model.Builder<Subject> {
        private String username;
        private final List<String> roles = new ArrayList<>();
        private final Map<String, Object> metadata = new HashMap<>();
        private boolean enabled = true;
        @Name("full_name") private String fullName;
        private String email;

        public Builder usernameMd5(Object... parts) { return username(md5Hex(safeUrnOf(parts))); }

        public Builder role(String role) { this.roles.add(role); return this; }

        public Builder roles(Iterable<String> roles) { Iterables.addAll(this.roles, roles); return this; }

        public Builder metadata(String key, Object value) { this.metadata.put(key, value); return this; }

        public Builder metadata(Map<String, Object> meta) { this.metadata.putAll(meta); return this; }

        @Override public Subject build() {
            return new Subject(requireNonNull(username, "username"), immutableList(roles), immutableResult(metadata), enabled, fullName, email);
        }
    }
}