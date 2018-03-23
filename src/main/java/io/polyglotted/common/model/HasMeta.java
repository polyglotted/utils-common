package io.polyglotted.common.model;

import java.util.Map;

@SuppressWarnings("unchecked")
public interface HasMeta<H> {
    SortedMapResult _meta();

    default boolean hasMeta() { return !_meta().isEmpty(); }

    default <T extends HasMeta<T>> T  withMetas(Map<String, Object> map) { map.forEach(this::withMeta); return (T) this; }

    default <T extends HasMeta<T>> T withMeta(String prop, Object value) { if (prop.startsWith("&")) { _meta().put(prop, value); } return (T) this; }
}