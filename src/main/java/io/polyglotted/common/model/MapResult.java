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

import static com.google.common.collect.ImmutableMap.copyOf;

@SuppressWarnings({"unused", "deprecation"})
public interface MapResult extends Map<String, Object> {

    static ImmutableMapResult immutableResult() { return new ImmutableMapResult(ImmutableMap.of()); }

    static ImmutableMapResult immutableResult(String k1, Object v1) { return (ImmutableMapResult) immutableResultBuilder().put(k1, v1).result(); }

    static ImmutableMapResult immutableResult(String k1, Object v1, String k2, Object v2) {
        return (ImmutableMapResult) immutableResultBuilder().put(k1, v1).put(k2, v2).result();
    }

    static ImmutableMapResult immutableResult(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return (ImmutableMapResult) immutableResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).result();
    }

    static ImmutableMapResult immutableResult(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        return (ImmutableMapResult) immutableResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).result();
    }

    static ImmutableMapResult immutableResult(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
        return (ImmutableMapResult) immutableResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).result();
    }

    static ImmutableMapResult immutableResult(Map<String, Object> map) { return (ImmutableMapResult) immutableResultBuilder().putAll(map).result(); }

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
    @NoArgsConstructor class SimpleMapResult extends LinkedHashMap<String, Object> implements ImmutableResult {
        public SimpleMapResult(Map<String, Object> m) { super(m); }

        @Override public Object put(String key, Object value) { if (value != null) { return super.put(key, value); } return null; }

        @Override public Object putIfAbsent(String key, Object value) { if (value != null) { return super.putIfAbsent(key, value); } return null; }

        @Override public void putAll(Map<? extends String, ?> map) {
            for (Map.Entry<? extends String, ?> e : map.entrySet()) { put(e.getKey(), e.getValue()); }
        }

        @Override public String toString() { return super.toString(); }

        @Override public ImmutableMapResult immutable() { return new ImmutableMapResult(copyOf(this)); }
    }

    @EqualsAndHashCode @RequiredArgsConstructor class ImmutableMapResult implements ImmutableResult {
        @Delegate(types = MapResult.class) private final ImmutableMap<String, Object> delegate;

        @Override public String toString() { return delegate.toString(); }

        @Override public ImmutableMapResult immutable() { return this; }
    }

    interface ImmutableResult extends MapResult {
        ImmutableMapResult immutable();
    }
}