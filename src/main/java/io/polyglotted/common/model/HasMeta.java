package io.polyglotted.common.model;

public interface HasMeta<H> {
    SortedMapResult _meta();

    default boolean hasMeta() { return !_meta().isEmpty(); }

    @SuppressWarnings("unchecked")
    default <T extends HasMeta<T>> T withMeta(String prop, Object value) { if (prop.startsWith("&")) { _meta().put(prop, value); } return (T) this; }
}