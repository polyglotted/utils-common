package io.polyglotted.common.model;

import com.google.common.collect.ImmutableMap;
import io.polyglotted.common.util.MapBuilder;
import io.polyglotted.common.util.MapBuilder.ImmutableMapBuilder;
import io.polyglotted.common.util.MapBuilder.SimpleMapBuilder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Iterables.getFirst;
import static io.polyglotted.common.util.Assertions.checkContains;
import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unused", "unchecked", "deprecation", "ConstantConditions"})
public interface MapResult extends Map<String, Object> {

    default <T> T first() { return requireNonNull(first(null)); }

    @SuppressWarnings("unchecked") default <T> T first(T def) { return (T) getFirst(values(), def); }

    default Entry<String, Object> firstEntry() { return requireNonNull(firstEntry(null)); }

    default Entry<String, Object> firstEntry(Entry<String, Object> def) { return getFirst(entrySet(), def); }

    default String optStr(String prop) { return stringVal(prop, false, null); }

    default String optStr(String prop, String defVal) { return stringVal(prop, false, defVal); }

    default String reqdStr(String prop) { return stringVal(prop, true, null); }

    default boolean boolVal(String prop, boolean defVal) { return asValue(prop, Boolean.class, defVal); }

    default int intVal(String prop, int defVal) { return asValue(prop, Integer.class, defVal); }

    default long longVal(String prop, long defVal) { return asValue(prop, Long.class, defVal); }

    default Long longStrVal(String prop) {
        Object value = get(prop);
        return value == null ? null : value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value));
    }

    default long longStrVal(String prop, long defaultValue) {
        Object value = get(prop);
        return value == null ? defaultValue : value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value));
    }

    default String stringVal(String prop, boolean required, String defVal) {
        return (String) getOrDefault(required ? reqdProp(prop) : prop, defVal);
    }

    default <T> T optValue(String prop) { return (T) get(prop); }

    default <T> T optValue(String prop, T defValue) { return (T) getOrDefault(prop, defValue); }

    default <T> T reqdValue(String prop) { return (T) get(reqdProp(prop)); }

    default <T> T asValue(String prop, Class<T> clazz, T defVal) { return clazz.cast(getOrDefault(prop, defVal)); }

    default String reqdProp(String prop) { return checkContains(this, prop); }

    default <T> T removeVal(String prop) { return (T) remove(reqdProp(prop)); }

    default <T> T removeIfExists(String prop) { return removeIfExists(prop, null); }

    default <T> T removeIfExists(String prop, T defVl) { return containsKey(prop) ? (T) remove(prop) : defVl; }

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
        @Delegate(types = MapInclude.class) private final ImmutableMap<String, Object> delegate;

        @Override public String toString() { return delegate.toString(); }

        @Override public ImmutableMapResult immutable() { return this; }

        //@formatter:off
        private interface MapInclude {
            int size();
            boolean isEmpty();
            boolean containsKey(Object key);
            boolean containsValue(Object value);
            Object get(Object key);
            Object put(String key, Object value);
            Object remove(Object key);
            void putAll(Map<? extends String, ?> m);
            void clear();
            Set<String> keySet();
            Collection<Object> values();
            Set<Entry<String, Object>> entrySet();
        } //@formatter:on
    }

    interface ImmutableResult extends MapResult {
        ImmutableMapResult immutable();
    }
}