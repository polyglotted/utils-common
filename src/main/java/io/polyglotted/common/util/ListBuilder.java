package io.polyglotted.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import static io.polyglotted.common.util.Assertions.checkGte;
import static io.polyglotted.common.util.ReflectionUtil.fieldValue;
import static java.util.Arrays.asList;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface ListBuilder<E, L extends Collection<E>> {

    @SuppressWarnings("UnusedReturnValue") ListBuilder<E, L> add(E elem);

    ListBuilder<E, L> addAll(Iterable<? extends E> elems);

    int size();

    L build();

    default L atleastOne() { checkGte(size(), 1, "size"); return build(); }

    static <E> ImmutableListBuilder<E> immutableListBuilder() { return new ImmutableListBuilder<>(); }

    static <E> ImmutableList<E> immutableList() { return ImmutableList.of(); }

    @SafeVarargs static <E> ImmutableList<E> immutableList(E... elems) { return immutableList(asList(elems)); }

    static <E> ImmutableList<E> immutableList(Iterable<? extends E> coll) { return ListBuilder.<E>immutableListBuilder().addAll(coll).build(); }

    static <E> SimpleListBuilder<E> simpleListBuilder() { return simpleListBuilder(LinkedList::new); }

    static <E> SimpleListBuilder<E> simpleListBuilder(Supplier<List<E>> supplier) { return new SimpleListBuilder<>(supplier.get()); }

    static <E> List<E> simpleList() { return new LinkedList<>(); }

    @SafeVarargs static <E> List<E> simpleList(E... elems) { return simpleList(asList(elems)); }

    static <E> List<E> simpleList(Iterable<? extends E> coll) { return ListBuilder.<E>simpleListBuilder().addAll(coll).build(); }

    static <E> SimpleSetBuilder<E> simpleSetBuilder() { return simpleSetBuilder(TreeSet::new); }

    static <E> SimpleSetBuilder<E> simpleSetBuilder(Supplier<Set<E>> supplier) { return new SimpleSetBuilder<>(supplier.get()); }

    @SafeVarargs static <E> Set<E> immutableSet(E... elems) { return immutableSet(asList(elems)); }

    static <E> Set<E> immutableSet(SimpleSetBuilder<E> builder) { return immutableSet(builder.build()); }

    static <E> Set<E> immutableSet(Iterable<E> iterable) { return ImmutableSet.copyOf(iterable); }

    @SafeVarargs static <E> Set<E> immutableSortedSet(E... elems) { return immutableSortedSet(asList(elems)); }

    static <E> Set<E> immutableSortedSet(SimpleSetBuilder<E> builder) { return immutableSortedSet(builder.build()); }

    static <E> Set<E> immutableSortedSet(Iterable<E> iterable) { return ImmutableSortedSet.copyOf(iterable); }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE) class ImmutableListBuilder<E> implements ListBuilder<E, ImmutableList<E>> {
        private final ImmutableList.Builder<E> builder = ImmutableList.builder();

        @Override public ListBuilder<E, ImmutableList<E>> add(E elem) { if (elem != null) builder.add(elem); return this; }

        @Override public ListBuilder<E, ImmutableList<E>> addAll(Iterable<? extends E> elems) { for (E elem : elems) { add(elem); } return this; }

        @Override public int size() { return fieldValue(builder, "size"); }

        @Override public ImmutableList<E> build() { return builder.build(); }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE) class SimpleListBuilder<E> implements ListBuilder<E, List<E>> {
        private final List<E> builder;

        @Override public ListBuilder<E, List<E>> add(E elem) { if (elem != null) builder.add(elem); return this; }

        @Override public ListBuilder<E, List<E>> addAll(Iterable<? extends E> elems) { for (E elem : elems) { add(elem); } return this; }

        @Override public int size() { return builder.size(); }

        @Override public List<E> build() { return builder; }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE) class SimpleSetBuilder<E> implements ListBuilder<E, Set<E>> {
        private final Set<E> builder;

        @Override public ListBuilder<E, Set<E>> add(E elem) { if (elem != null) builder.add(elem); return this; }

        @Override public ListBuilder<E, Set<E>> addAll(Iterable<? extends E> elems) { for (E elem : elems) { add(elem); } return this; }

        @Override public int size() { return builder.size(); }

        @Override public Set<E> build() { return builder; }
    }
}