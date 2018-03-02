package io.polyglotted.common.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkArgument;
import static io.polyglotted.common.util.ReflectionUtil.isEnum;

@SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
public abstract class EnumCache {
    private static final ConcurrentMap<Class<?>, Map<String, Object>> enumValuesMap = new ConcurrentHashMap<>();

    public static <E extends Enum> E fetchEnumFor(Class<E> enumClass, String value) { return (E) fetchEnumValueFor(enumClass, value); }

    public static Object fetchEnumValueFor(Class enumClass, String value) {
        if (!enumValuesMap.containsKey(enumClass)) putEnumValuesInMap(enumClass);
        return enumValuesMap.get(enumClass).get(value);
    }

    private static <E extends Enum> void putEnumValuesInMap(Class<E> enumClass) {
        checkArgument(isEnum(enumClass));
        ImmutableMap.Builder<String, Object> values = ImmutableMap.builder();
        for (E value : enumClass.getEnumConstants()) {
            values.put(value.name(), value);
            if (!value.toString().equals(value.name())) { values.put(value.toString(), value); }
        }
        enumValuesMap.putIfAbsent(enumClass, values.build());
    }
}