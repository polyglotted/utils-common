package io.polyglotted.common.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.polyglotted.common.util.NullUtil.nonNull;

@SuppressWarnings({"unused", "unchecked", "Guava", "StaticPseudoFunctionalStyleMethod", "ConstantConditions"})
public abstract class CollUtil {

    public static <K, V> Map<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> function) { return Maps.uniqueIndex(values, function); }

    public static <T> Collection<T> asColl(Object value, Collection<T> defaulted) { return nonNull((Collection<T>) value, defaulted); }

    public static <F, T> Collection<T> transformColl(Collection<F> coll, Function<? super F, T> fn) { return Collections2.transform(coll, fn); }

    public static <E> Collection<E> filterColl(Collection<E> coll, Predicate<? super E> predicate) { return Collections2.filter(coll, predicate); }

    public static <F, T> List<T> transformList(List<F> list, Function<? super F, ? extends T> fn) { return Lists.transform(list, fn); }

    public static <E> Iterable<E> filter(Iterable<E> list, Predicate<? super E> predicate) { return Iterables.filter(list, predicate); }

    public static <E> E firstOf(Iterable<E> list) { return Iterables.getFirst(list, null); }
}