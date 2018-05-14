package io.polyglotted.common.util;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class NullUtil {

    public static <T> T nonNull(T nullable, Supplier<T> supplier) { return nullable != null ? nullable : supplier.get(); }

    public static <T> T nonNull(T nullable, T defValue) { return nullable != null ? nullable : defValue; }

    public static <T> T nonNull(T first, T second, T third) { return first != null ? first : second != null ? second : third; }

    @SafeVarargs public static <T> T nonNullIn(T... list) { for (T t : list) { if (t != null) return t; } return null; }

    public static <K, V> V nonNullFn(K nullable, Function<K, V> function, V defVal) { return nullable != null ? function.apply(nullable) : defVal; }

    public static <K, V> V nonNullFn(K nullable, Function<K, V> function, Supplier<V> supplier) {
        return nullable != null ? function.apply(nullable) : supplier.get();
    }
}