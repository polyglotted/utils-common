package io.polyglotted.common.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import io.polyglotted.common.model.MapResult;
import io.polyglotted.common.model.MapResult.ImmutableMapResult;
import io.polyglotted.common.model.MapResult.ImmutableResult;
import io.polyglotted.common.model.MapResult.SimpleMapResult;
import io.polyglotted.common.model.Pair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static io.polyglotted.common.util.ReflectionUtil.fieldValue;

@SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
public interface MapBuilder<K, V, M extends Map<K, V>, MB extends MapBuilder<K, V, M, MB>> {
    MB put(K key, V value);

    default MB putList(K key, List<?> values) {
        if (values != null && !values.isEmpty()) put(key, (V) values); return (MB) this;
    }

    default MB putAll(Map<K, V> map) {
        for (Map.Entry<K, V> e : map.entrySet()) { put(e.getKey(), e.getValue()); } return (MB) this;
    }

    default MB putTransformed(Map<K, V> map, PairTransformer transformer) {
        for (Map.Entry<K, V> e : map.entrySet()) { Pair<K, V> pair = transformer.transform(e); put(pair._a, pair._b); } return (MB) this;
    }

    int size();

    M build();

    ImmutableMap<K, V> immutable();

    MapResult result();

    ImmutableResult immutableResult();

    static <K, V, M extends Map<K, V>, MB extends MapBuilder<K, V, M, MB>> ImmutableBiMap<K, V> immutableBiMap(MapBuilder<K, V, M, MB> builder) {
        return immutableBiMap(builder.build());
    }

    static <K, V> ImmutableBiMap<K, V> immutableBiMap(Map<K, V> map) { return ImmutableBiMap.copyOf(map); }

    static <K, V> ImmutableBiMap<K, V> immutableBiMap() { return ImmutableBiMap.of(); }

    static <K, V, M extends Map<K, V>, MB extends MapBuilder<K, V, M, MB>> ImmutableSortedMap<K, V> immutableSortedMap(MapBuilder<K, V, M, MB> bl) {
        return immutableSortedMap(bl.build());
    }

    static <K, V> ImmutableSortedMap<K, V> immutableSortedMap(Map<K, V> map) { return ImmutableSortedMap.copyOf(map); }

    static <K, V> ImmutableSortedMap<K, V> immutableSortedMap() { return ImmutableSortedMap.of(); }

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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE) class ImmutableMapBuilder<K, V>
        implements MapBuilder<K, V, ImmutableMap<K, V>, ImmutableMapBuilder<K, V>> {
        private final ImmutableMap.Builder<K, V> builder;

        @Override public ImmutableMapBuilder<K, V> put(K key, V value) { if (value != null) builder.put(key, value); return this; }

        @Override public int size() { return fieldValue(builder, "size"); }

        @Override public ImmutableMap<K, V> build() { return builder.build(); }

        @Override public ImmutableMap<K, V> immutable() { return builder.build(); }

        @Override public MapResult result() { return immutableResult(); }

        @Override public ImmutableResult immutableResult() { return new ImmutableMapResult((ImmutableMap<String, Object>) builder.build()); }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE) @Accessors(fluent = true) class SimpleMapBuilder<K, V>
        implements MapBuilder<K, V, Map<K, V>, SimpleMapBuilder<K, V>> {
        @Getter private final Map<K, V> builder;

        public boolean containsKey(K key) { return builder.containsKey(key); }

        public V get(K key) { return builder.get(key); }

        public V getOrDefault(K key, V defaultValue) { return builder.getOrDefault(key, defaultValue); }

        public V remove(K key) { return builder.remove(key); }

        @Override public SimpleMapBuilder<K, V> put(K key, V value) { if (value != null) builder.put(key, value); return this; }

        @Override public int size() { return builder.size(); }

        @Override public Map<K, V> build() { return builder; }

        @Override public ImmutableMap<K, V> immutable() { return ImmutableMap.copyOf(builder); }

        @Override public MapResult result() {
            return builder instanceof MapResult ? (MapResult) builder : new SimpleMapResult((Map<String, Object>) builder);
        }

        @Override public ImmutableResult immutableResult() { return new ImmutableMapResult(ImmutableMap.copyOf((Map<String, Object>) builder)); }
    }

    interface PairTransformer<K, V> {
        Pair<K, V> transform(Map.Entry<K, V> entry);
    }
}