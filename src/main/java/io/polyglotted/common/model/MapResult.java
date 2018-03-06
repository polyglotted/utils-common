package io.polyglotted.common.model;

import com.google.common.collect.ImmutableMap;
import io.polyglotted.common.util.MapBuilder;
import io.polyglotted.common.util.MapBuilder.ImmutableMapBuilder;
import io.polyglotted.common.util.MapBuilder.SimpleMapBuilder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({"unused", "deprecation"})
public interface MapResult extends Map<String, Object> {

    static MapResult immutableResult() { return new ImmutableMapResult(ImmutableMap.of()); }

    static MapResult immutableResult(String k1, Object v1) { return immutableResultBuilder().put(k1, v1).result(); }

    static MapResult immutableResult(String k1, Object v1, String k2, Object v2) { return immutableResultBuilder().put(k1, v1).put(k2, v2).result(); }

    static MapResult immutableResult(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return immutableResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).result();
    }

    static MapResult immutableResult(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        return immutableResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).result();
    }

    static MapResult immutableResult(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
        return immutableResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).result();
    }

    static MapResult immutableResult(Map<String, Object> map) { return immutableResultBuilder().putAll(map).result(); }

    static ImmutableMapBuilder<String, Object> immutableResultBuilder() { return MapBuilder.immutableMapBuilder(); }

    static MapResult simpleResult() { return new SimpleMapResult(); }

    static MapResult simpleResult(String k1, Object v1) { return simpleResultBuilder().put(k1, v1).result(); }

    static MapResult simpleResult(String k1, Object v1, String k2, Object v2) { return simpleResultBuilder().put(k1, v1).put(k2, v2).result(); }

    static MapResult simpleResult(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return simpleResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).result();
    }

    static MapResult simpleResult(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        return simpleResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).result();
    }

    static MapResult simpleResult(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
        return simpleResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).result();
    }

    static MapResult simpleResult(Map<String, Object> map) { return simpleResultBuilder().putAll(map).result(); }

    static SimpleMapBuilder<String, Object> simpleResultBuilder() { return MapBuilder.simpleMapBuilder(SimpleMapResult::new); }

    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor class SimpleMapResult extends LinkedHashMap<String, Object> implements MapResult {
        public SimpleMapResult(Map<String, Object> m) { super(m); }

        @Override public String toString() { return super.toString(); }
    }

    @EqualsAndHashCode @RequiredArgsConstructor class ImmutableMapResult implements MapResult {
        @Delegate(types = MapResult.class) private final ImmutableMap<String, Object> delegate;

        @Override public String toString() { return delegate.toString(); }
    }
}