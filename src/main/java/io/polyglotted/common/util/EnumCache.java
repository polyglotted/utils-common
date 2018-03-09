package io.polyglotted.common.util;

import io.polyglotted.common.model.MapResult;
import io.polyglotted.common.util.MapBuilder.ImmutableMapBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.polyglotted.common.util.Assertions.checkBool;
import static io.polyglotted.common.util.MapBuilder.immutableMapBuilder;
import static io.polyglotted.common.util.ReflectionUtil.isEnum;

@SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
public abstract class EnumCache {
    private static final ConcurrentMap<Class<?>, MapResult> enumValuesMap = new ConcurrentHashMap<>();

    public static <E extends Enum> E fetchEnumFor(Class<E> enumClass, String value) { return (E) fetchEnumValueFor(enumClass, value); }

    public static Object fetchEnumValueFor(Class enumClass, String value) {
        if (!enumValuesMap.containsKey(enumClass)) putEnumValuesInMap(enumClass);
        return enumValuesMap.get(enumClass).get(value);
    }

    private static <E extends Enum> void putEnumValuesInMap(Class<E> enumClass) {
        checkBool(isEnum(enumClass), enumClass + " not an enum class");
        ImmutableMapBuilder<String, Object> values = immutableMapBuilder();
        for (E value : enumClass.getEnumConstants()) {
            values.put(value.name(), value);
            if (!value.toString().equals(value.name())) { values.put(value.toString(), value); }
        }
        enumValuesMap.putIfAbsent(enumClass, values.result());
    }
}