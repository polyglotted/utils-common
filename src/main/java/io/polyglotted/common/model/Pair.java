package io.polyglotted.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;

import static io.polyglotted.common.util.ListBuilder.immutableList;

@Getter
@Accessors(fluent = true)
@SuppressWarnings({"unused", "WeakerAccess", "unchecked"})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Pair<A, B> {
    @Getter public final A _a;
    @Getter public final B _b;

    public static <A, B> Pair<A, B> pair(A a, B b) { return new Pair<>(a, b); }

    public <T> List<T> asList() { return immutableList((T) _a, (T) _b); }

    @Override
    public boolean equals(Object o) {
        return this == o || (!(o == null || getClass() != o.getClass()) && Objects.equals(_a, ((Pair) o)._a) && Objects.equals(_b, ((Pair) o)._b));
    }

    @Override
    public int hashCode() { return Objects.hash(_a, _b); }
}