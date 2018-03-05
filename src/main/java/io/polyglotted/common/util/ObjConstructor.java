package io.polyglotted.common.util;

import io.polyglotted.common.model.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.MapRetriever.asMap;
import static io.polyglotted.common.util.ReflectionUtil.create;
import static io.polyglotted.common.util.ReflectionUtil.fieldValue;
import static io.polyglotted.common.util.ReflectionUtil.getCollTypeArg;
import static io.polyglotted.common.util.ReflectionUtil.getMapTypeArgs;
import static io.polyglotted.common.util.ReflectionUtil.isAssignable;
import static io.polyglotted.common.util.Sanitizer.isSanitizable;
import static io.polyglotted.common.util.Sanitizer.sanitize;

@SuppressWarnings("unused")
public abstract class ObjConstructor {

    @SuppressWarnings("unchecked") public static <T> T constructSimple(Map<String, Object> map, T builder) {
        for (Field field : builder.getClass().getDeclaredFields()) {
            Object value = map.get(field.getName());
            if (value != null) { fieldValue(builder, field, sanitize(field.getType(), value)); }
        }
        return builder;
    }

    @SuppressWarnings("unchecked") public static <T> T construct(Map<String, Object> map, T builder) {
        for (Field field : builder.getClass().getDeclaredFields()) {
            Object value = map.get(field.getName());
            if (value != null) {
                if (value instanceof Collection) {
                    if (isAssignable(Set.class, field.getType())) {
                        fieldValue(builder, field, collectInto(field, asColl(value, immutableList()), new LinkedHashSet<>()));
                    }
                    else if (isAssignable(List.class, field.getType())) {
                        fieldValue(builder, field, collectInto(field, asColl(value, immutableList()), new ArrayList<>()));
                    }
                    else {
                        fieldValue(builder, field, value);
                    }
                }
                else if (value instanceof Map) {
                    if (isAssignable(Map.class, field.getType())) { fieldValue(builder, field, mapInto(field, asMap(value))); }
                    else { fieldValue(builder, field, construct((Map<String, Object>) value, create(field.getType()))); }
                }
                else { fieldValue(builder, field, sanitize(field.getType(), value)); }
            }
        }
        return builder;
    }

    @SuppressWarnings("unchecked") private static <T, C extends Collection<T>> C collectInto(Field field, List<?> objects, C result) {
        Class<?> elemClass = getCollTypeArg(field);
        for (Object obj : objects) { result.add(getElem(elemClass, obj)); }
        return result;
    }

    @SuppressWarnings("unchecked") private static Map<Object, Object> mapInto(Field field, Map<?, ?> map) {
        final Map<Object, Object> result = new LinkedHashMap<>();
        Pair<Class<?>, Class<?>> mapTypeArgs = getMapTypeArgs(field);
        for (Entry<?, ?> entry : map.entrySet()) { result.put(getElem(mapTypeArgs._a, entry.getKey()), getElem(mapTypeArgs._b, entry.getValue())); }
        return result;
    }

    @SuppressWarnings("unchecked") private static <T> T getElem(Class<?> elemClass, Object obj) {
        return isSanitizable(elemClass) ? (T) sanitize(elemClass, obj) : ((obj instanceof Map && !Object.class.equals(elemClass)) ?
            construct(asMap(obj), (T) create(elemClass)) : (T) obj);
    }

    @SuppressWarnings("unchecked") private static <C extends Collection<?>> C asColl(Object col, C defVl) { return col == null ? defVl : ((C) col); }
}