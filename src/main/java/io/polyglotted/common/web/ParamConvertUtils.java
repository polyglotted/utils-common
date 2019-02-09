package io.polyglotted.common.web;

import com.google.common.base.Defaults;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage") abstract class ParamConvertUtils {
    private static final Map<Class<?>, Method> PRIMITIVES_PARSE_METHODS;

    // Setup methods for converting string into primitive/boxed types
    static {
        Map<Class<?>, Method> methods = Maps.newIdentityHashMap();
        for (Class<?> wrappedType : Primitives.allWrapperTypes()) {
            try {
                methods.put(wrappedType, wrappedType.getMethod("valueOf", String.class));
            } catch (NoSuchMethodException e) {
                // Void and Character has no valueOf. It's ok to ignore them
            }
        }
        PRIMITIVES_PARSE_METHODS = methods;
    }

    static Function<String, Object> createPathParamConverter(final Type resultType) {
        if (!(resultType instanceof Class)) { throw new IllegalArgumentException("Unsupported @PathParam type " + resultType); }
        return value -> ConvertUtils.convert(value, (Class<?>) resultType);
    }

    static Function<List<String>, Object> createQueryParamConverter(Type resultType) { return createListConverter(resultType); }

    private static Function<List<String>, Object> createListConverter(Type resultType) {
        TypeToken<?> typeToken = TypeToken.of(resultType);

        // Use boxed type if raw type is primitive type. Otherwise the type won't change.
        Class<?> resultClass = typeToken.getRawType();
        if (resultClass == String.class) {
            return new BasicConverter(Defaults.defaultValue(resultClass)) {
                @Override
                protected Object convert(String value) { return value; }
            };
        }

        Function<List<String>, Object> converter = createPrimitiveTypeConverter(resultClass);
        if (converter != null) { return converter; }

        converter = createStringConstructorConverter(resultClass);
        if (converter != null) { return converter; }

        converter = createStringMethodConverter(resultClass);
        if (converter != null) { return converter; }

        converter = createCollectionConverter(typeToken);
        if (converter != null) { return converter; }

        throw new IllegalArgumentException("Unsupported type " + typeToken);
    }

    private static Function<List<String>, Object> createPrimitiveTypeConverter(Class<?> resultClass) {
        Object defaultValue = Defaults.defaultValue(resultClass);
        final Class<?> boxedType = Primitives.wrap(resultClass);

        if (!Primitives.isWrapperType(boxedType)) { return null; }

        return new BasicConverter(defaultValue) {
            @Override
            protected Object convert(String value) throws Exception {
                Method method = PRIMITIVES_PARSE_METHODS.get(boxedType);
                if (method != null) {
                    // It's primitive/wrapper type (except char)
                    return method.invoke(null, value);
                }
                // One exception is char type
                if (boxedType == Character.class) { return value.charAt(0); }

                return null;
            }
        };
    }

    private static Function<List<String>, Object> createStringConstructorConverter(Class<?> resultClass) {
        try {
            final Constructor<?> constructor = resultClass.getConstructor(String.class);
            return new BasicConverter(Defaults.defaultValue(resultClass)) {
                @Override
                protected Object convert(String value) throws Exception {
                    return constructor.newInstance(value);
                }
            };
        } catch (Exception e) {
            return null;
        }
    }

    private static Function<List<String>, Object> createStringMethodConverter(Class<?> resultClass) {
        Method method;
        try {
            method = resultClass.getMethod("valueOf", String.class);
        } catch (Exception e) {
            try {
                method = resultClass.getMethod("fromString", String.class);
            } catch (Exception ex) {
                return null;
            }
        }

        final Method convertMethod = method;
        return new BasicConverter(Defaults.defaultValue(resultClass)) {
            @Override
            protected Object convert(String value) throws Exception {
                return convertMethod.invoke(null, value);
            }
        };
    }

    private static Function<List<String>, Object> createCollectionConverter(TypeToken<?> resultType) {
        final Class<?> rawType = resultType.getRawType();

        // Collection. It must be List or Set
        if (rawType != List.class && rawType != Set.class && rawType != SortedSet.class) { return null; }

        // Must be ParameterizedType
        if (!(resultType.getType() instanceof ParameterizedType)) { return null; }

        // Must have 1 type parameter
        ParameterizedType type = (ParameterizedType) resultType.getType();
        if (type.getActualTypeArguments().length != 1) { return null; }

        // For SortedSet, the entry type must be Comparable.
        Type elementType = type.getActualTypeArguments()[0];
        if (rawType == SortedSet.class && !Comparable.class.isAssignableFrom(TypeToken.of(elementType).getRawType())) { return null; }

        // Get the converter for the collection element.
        final Function<List<String>, Object> elementConverter = createQueryParamConverter(elementType);
        if (elementConverter == null) { return null; }

        return new Function<List<String>, Object>() {
            @Override
            public Object apply(List<String> values) {
                ImmutableCollection.Builder<? extends Comparable> builder;
                if (rawType == List.class) { builder = ImmutableList.builder(); }
                else if (rawType == Set.class) { builder = ImmutableSet.builder(); }
                else { builder = ImmutableSortedSet.naturalOrder(); }

                for (String value : values) {
                    add(builder, elementConverter.apply(ImmutableList.of(value)));
                }
                return builder.build();
            }

            @SuppressWarnings("unchecked")
            private <T> void add(ImmutableCollection.Builder<T> builder, Object element) { builder.add((T) element); }
        };
    }

    @RequiredArgsConstructor
    private abstract static class BasicConverter implements Function<List<String>, Object> {
        private final Object defaultValue;

        @Override
        @SneakyThrows
        public final Object apply(List<String> values) { return (values.isEmpty()) ? defaultValue : convert(values.get(0)); }

        protected abstract Object convert(String value) throws Exception;
    }
}
