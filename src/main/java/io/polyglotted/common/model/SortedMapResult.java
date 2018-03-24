package io.polyglotted.common.model;

import io.polyglotted.common.model.MapResult.ImmutableResult;
import io.polyglotted.common.util.MapBuilder;
import io.polyglotted.common.util.MapBuilder.SimpleMapBuilder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static io.polyglotted.common.util.MapBuilder.immutableMap;

@SuppressWarnings("unused")
public interface SortedMapResult extends ImmutableResult, SortedMap<String, Object> {
    static SortedMapResult treeResult() { return new TreeMapResult(); }

    static SortedMapResult treeResult(String k1, Object v1) { return (SortedMapResult) treeResultBuilder().put(k1, v1).result(); }

    static SortedMapResult treeResult(String k1, Object v1, String k2, Object v2) {
        return (SortedMapResult) treeResultBuilder().put(k1, v1).put(k2, v2).result();
    }

    static SortedMapResult treeResult(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return (SortedMapResult) treeResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).result();
    }

    static SortedMapResult treeResult(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        return (SortedMapResult) treeResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).result();
    }

    static SortedMapResult treeResult(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
        return (SortedMapResult) treeResultBuilder().put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).result();
    }

    static SortedMapResult treeResult(Map<String, Object> map) { return (SortedMapResult) treeResultBuilder().putAll(map).result(); }

    static SimpleMapBuilder<String, Object> treeResultBuilder() { return MapBuilder.simpleMapBuilder(TreeMapResult::new); }

    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor class TreeMapResult extends TreeMap<String, Object> implements SortedMapResult {
        @Override public Object put(String key, Object value) { if (value != null) { return super.put(key, value); } return null; }

        @Override public Object putIfAbsent(String key, Object value) { if (value != null) { return super.putIfAbsent(key, value); } return null; }

        @Override public void putAll(Map<? extends String, ?> map) {
            for (Map.Entry<? extends String, ?> e : map.entrySet()) { put(e.getKey(), e.getValue()); }
        }

        @Override public String toString() { return super.toString(); }

        @Override public ImmutableMapResult immutable() { return new ImmutableMapResult(immutableMap(this)); }
    }
}