package io.polyglotted.common.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Predicates.not;
import static io.polyglotted.common.util.NullUtil.nonNull;

@SuppressWarnings({"unused", "unchecked", "Guava", "StaticPseudoFunctionalStyleMethod", "ConstantConditions", "WeakerAccess"})
public abstract class CollUtil {

    public static <K, V> Map<K, V> filterKeys(Map<K, V> map, final Predicate<? super K> predicate) { return Maps.filterKeys(map, predicate); }

    public static <K, V> Map<K, V> filterKeysNeg(Map<K, V> map, final Predicate<? super K> predicate) { return Maps.filterKeys(map, not(predicate)); }

    public static <K, V> Map<K, V> filterValues(Map<K, V> map, final Predicate<? super V> predicate) { return Maps.filterValues(map, predicate); }

    public static <K, V> Map<K, V> filterValuesNeg(Map<K, V> map, final Predicate<? super V> pred) { return Maps.filterValues(map, not(pred)); }

    public static <K, V> Map<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> function) { return Maps.uniqueIndex(values, function); }

    public static <T> Collection<T> asColl(Object value, Collection<T> defaulted) { return nonNull((Collection<T>) value, defaulted); }

    public static <E> Collection<E> filterColl(Collection<E> coll, Predicate<? super E> predicate) { return Collections2.filter(coll, predicate); }

    public static <E> Collection<E> filterCollNeg(Collection<E> coll, Predicate<? super E> pred) { return Collections2.filter(coll, not(pred)); }

    public static <F, T> Collection<T> transformColl(Collection<F> coll, Function<? super F, T> fn) { return Collections2.transform(coll, fn); }

    public static <F, T> List<T> transformList(List<F> list, Function<? super F, ? extends T> fn) { return Lists.transform(list, fn); }

    public static <F, T> FluentIterable<T> transform(Iterable<F> list, final Function<? super F, ? extends T> fn) {
        return (FluentIterable<T>) Iterables.transform(list, fn);
    }

    public static <E> FluentIterable<E> filter(Iterable<E> list, Predicate<? super E> predicate) {
        return (FluentIterable<E>) Iterables.filter(list, predicate);
    }

    public static <E> FluentIterable<E> filterNeg(Iterable<E> list, Predicate<? super E> predicate) {
        return (FluentIterable<E>) Iterables.filter(list, not(predicate));
    }

    public static <T> FluentIterable<T> concat(Iterable<? extends T>... inputs) { return FluentIterable.concat(inputs); }

    public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) { return FluentIterable.concat(a, b); }

    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) { return Iterables.toArray(iterable, type); }

    public static <E> E firstOf(Iterable<E> list) { return firstOf(list, null); }

    public static <E> E firstOf(Iterable<E> list, E defaulted) { return Iterables.getFirst(list, defaulted); }
}