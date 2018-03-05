package io.polyglotted.common.util;

import com.google.common.collect.ImmutableMap;
import io.polyglotted.common.model.MapResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static io.polyglotted.common.model.MapResult.immutableResult;
import static io.polyglotted.common.model.MapResult.simpleResult;
import static io.polyglotted.common.util.ReflectionUtil.fieldValue;

@SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
public interface MapBuilder<K, V, M extends Map<K, V>> {
    MapBuilder<K, V, M> put(K key, V value);

    MapBuilder<K, V, M> putList(K key, List<?> values);

    MapBuilder<K, V, M> putAll(Map<K, V> map);

    int size();

    M build();

    MapResult result();

    static <K, V> ImmutableMapBuilder<K, V> immutableMapBuilder() { return immutableMapBuilder(ImmutableMap::builder); }

    static <K, V> ImmutableMapBuilder<K, V> immutableMapBuilder(Supplier<ImmutableMap.Builder<K, V>> s) { return new ImmutableMapBuilder<>(s.get()); }

    static <K, V> ImmutableMap<K, V> immutableMap() { return ImmutableMap.of(); }

    static <K, V> ImmutableMap<K, V> immutableMap(K k1, V v1) { return MapBuilder.<K, V>immutableMapBuilder().put(k1, v1).build(); }

    static <K, V> ImmutableMap<K, V> immutableMap(K k1, V v1, K k2, V v2) {
        return MapBuilder.<K, V>immutableMapBuilder().put(k1, v1).put(k2, v2).build();
    }

    static <K, V> ImmutableMap<K, V> immutableMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return MapBuilder.<K, V>immutableMapBuilder().put(k1, v1).put(k2, v2).put(k3, v3).build();
    }

    static <K, V> ImmutableMap<K, V> immutableMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return MapBuilder.<K, V>immutableMapBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).build();
    }

    static <K, V> ImmutableMap<K, V> immutableMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return MapBuilder.<K, V>immutableMapBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).build();
    }

    static <K, V> ImmutableMap<K, V> immutableMap(Map<K, V> map) { return MapBuilder.<K, V>immutableMapBuilder().putAll(map).build(); }

    static <K, V> SimpleMapBuilder<K, V> simpleMapBuilder() { return simpleMapBuilder(LinkedHashMap::new); }

    static <K, V> SimpleMapBuilder<K, V> simpleMapBuilder(Supplier<Map<K, V>> supplier) { return new SimpleMapBuilder<>(supplier.get()); }

    static <K, V> Map<K, V> simpleMap() { return new LinkedHashMap<>(); }

    static <K, V> Map<K, V> simpleMap(K k1, V v1) { return MapBuilder.<K, V>simpleMapBuilder().put(k1, v1).build(); }

    static <K, V> Map<K, V> simpleMap(K k1, V v1, K k2, V v2) { return MapBuilder.<K, V>simpleMapBuilder().put(k1, v1).put(k2, v2).build(); }

    static <K, V> Map<K, V> simpleMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return MapBuilder.<K, V>simpleMapBuilder().put(k1, v1).put(k2, v2).put(k3, v3).build();
    }

    static <K, V> Map<K, V> simpleMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return MapBuilder.<K, V>simpleMapBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).build();
    }

    static <K, V> Map<K, V> simpleMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return MapBuilder.<K, V>simpleMapBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).build();
    }

    static <K, V> Map<K, V> simpleMap(Map<K, V> map) { return MapBuilder.<K, V>simpleMapBuilder().putAll(map).build(); }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE) class ImmutableMapBuilder<K, V> implements MapBuilder<K, V, ImmutableMap<K, V>> {
        private final ImmutableMap.Builder<K, V> builder;

        @Override public MapBuilder<K, V, ImmutableMap<K, V>> put(K key, V value) { if (value != null) builder.put(key, value); return this; }

        @Override public MapBuilder<K, V, ImmutableMap<K, V>> putList(K key, List<?> values) {
            if (values != null && !values.isEmpty()) builder.put(key, (V) values); return this;
        }

        @Override public MapBuilder<K, V, ImmutableMap<K, V>> putAll(Map<K, V> map) {
            for (Map.Entry<K, V> e : map.entrySet()) { put(e.getKey(), e.getValue()); } return this;
        }

        @Override public int size() { return fieldValue(builder, "size"); }

        @Override public ImmutableMap<K, V> build() { return builder.build(); }

        @Override public MapResult result() { return immutableResult((ImmutableMap<String, Object>) builder.build()); }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE) class SimpleMapBuilder<K, V> implements MapBuilder<K, V, Map<K, V>> {
        private final Map<K, V> builder;

        @Override public MapBuilder<K, V, Map<K, V>> put(K key, V value) { if (value != null) builder.put(key, value); return this; }

        @Override public MapBuilder<K, V, Map<K, V>> putList(K key, List<?> values) {
            if (values != null && !values.isEmpty()) builder.put(key, (V) values); return this;
        }

        @Override public MapBuilder<K, V, Map<K, V>> putAll(Map<K, V> map) {
            for (Map.Entry<K, V> e : map.entrySet()) { put(e.getKey(), e.getValue()); } return this;
        }

        @Override public int size() { return builder.size(); }

        @Override public Map<K, V> build() { return builder; }

        @Override public MapResult result() { return builder instanceof MapResult ? (MapResult) builder : simpleResult((Map<String, Object>) builder); }
    }
}