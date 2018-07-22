package io.polyglotted.common.model;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Map;

import static io.polyglotted.common.util.NullUtil.nonNullFn;
import static io.polyglotted.common.util.ReflectionUtil.annotation;
import static io.polyglotted.common.util.ReflectionUtil.create;
import static io.polyglotted.common.util.ReflectionUtil.declaredField;
import static io.polyglotted.common.util.ReflectionUtil.declaredMethod;
import static io.polyglotted.common.util.ReflectionUtil.detectValueClass;
import static io.polyglotted.common.util.ReflectionUtil.safeInvoke;
import static io.polyglotted.common.util.Sanitizer.sanitize;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface Builder<T> {
    T build();

    @Documented @Retention(RUNTIME)
    @Target(FIELD) @interface Name {
        String value();
    }

    static <T, C extends Builder<T>> T buildWith(Map<String, Object> mapResult, Class<C> clazz) { return buildWith(mapResult, clazz, false); }

    @SuppressWarnings("unchecked") static <T, C extends Builder<T>> T buildWith(Map<String, Object> mapResult, Class<C> clazz, boolean inclMeta) {
        T built = builder(mapResult, clazz).build();
        return (inclMeta && built instanceof HasMeta) ? (T) ((HasMeta) built).withMetas(mapResult) : built;
    }

    @SuppressWarnings("unchecked") static <T, C extends Builder<T>> C builder(Map<String, Object> mapResult, Class<C> clazz) {
        C builder = (C) create(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            Object value = mapResult.get(field.getName());
            if (value == null) { value = mapResult.get(nonNullFn(annotation(field, Builder.Name.class), Builder.Name::value, "_$")); }

            if (value != null) {
                Class<?> valueClass = detectValueClass(value, () -> declaredField(clazz, field.getName()));
                try {
                    safeInvoke(declaredMethod(clazz, field.getName(), new Class[]{valueClass}), builder, sanitize(valueClass, value));
                } catch (Exception ex) { throw new RuntimeException("failed invoke " + field.getName(), ex); }
            }
        }
        return builder;
    }
}