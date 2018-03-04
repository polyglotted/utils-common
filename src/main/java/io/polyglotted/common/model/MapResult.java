package io.polyglotted.common.model;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({"unused", "deprecation"})
public interface MapResult extends Map<String, Object> {

    static MapResult immutableResult() { return new ImmutableMapResult(ImmutableMap.of()); }

    static MapResult immutableResult(ImmutableMap<String, Object> map) { return new ImmutableMapResult(map); }

    static MapResult simpleResult() { return new SimpleMapResult(); }

    static MapResult simpleResult(Map<String, Object> map) { return new SimpleMapResult(map); }

    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor class SimpleMapResult extends LinkedHashMap<String, Object> implements MapResult {
        SimpleMapResult(Map<? extends String, ?> m) { super(m); }

        @Override public String toString() { return super.toString(); }
    }

    @EqualsAndHashCode @RequiredArgsConstructor class ImmutableMapResult implements MapResult {
        @Delegate(types = MapResult.class) private final ImmutableMap<String, Object> delegate;

        @Override public String toString() { return delegate.toString(); }
    }
}