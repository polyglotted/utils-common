package io.polyglotted.common.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import io.polyglotted.common.model.MapResult;
import io.polyglotted.common.model.Pair;
import io.polyglotted.common.util.ListBuilder.ImmutableListBuilder;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.polyglotted.common.model.MapResult.immutableResult;
import static io.polyglotted.common.model.Pair.pair;
import static io.polyglotted.common.util.Assertions.checkBool;
import static io.polyglotted.common.util.Assertions.checkContains;
import static io.polyglotted.common.util.CollUtil.toArray;
import static io.polyglotted.common.util.EnumCache.fetchEnumValueFor;
import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.ListBuilder.immutableListBuilder;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.ReflectionUtil.declaredField;
import static io.polyglotted.common.util.ReflectionUtil.fieldValue;
import static io.polyglotted.common.util.ReflectionUtil.isAssignable;
import static io.polyglotted.common.util.ReflectionUtil.safeClass;
import static io.polyglotted.common.util.ReflectionUtil.safeFieldValue;
import static io.polyglotted.common.util.StrUtil.safePrefix;
import static io.polyglotted.common.util.StrUtil.safeSuffix;

@SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
public abstract class MapRetriever {
    public static final Class<Map<String, Object>> MAP_CLASS = (Class<Map<String, Object>>) new TypeToken<Map<String, Object>>() {}.getRawType();
    public static final Class<Map<String, String>> STRMAP_CLASS = (Class<Map<String, String>>) new TypeToken<Map<String, String>>() {}.getRawType();
    public static final Class<List<Map<String, Object>>> MAP_LIST_CLASS = (Class<List<Map<String, Object>>>)
        new TypeToken<List<Map<String, Object>>>() {}.getRawType();
    public static final Class<List<String>> STRING_LIST_CLASS = (Class<List<String>>) new TypeToken<List<String>>() {}.getRawType();
    protected static Pattern LIST_PATTERN = Pattern.compile("\\[(\\d+)\\]");

    public static Map<String, Object> deepSet(Map<String, Object> map, String property, Object newValue) {
        if (!property.contains(".")) { map.put(property, newValue); return map; }
        checkBool(!property.startsWith("."), "property cannot begin with a dot");

        try {
            String[] parts = property.split("\\.");
            Map<String, Object> child = map;
            for (int i = 0; i < parts.length - 1; i++) {
                Map<String, Object> child2 = MAP_CLASS.cast(child.get(parts[i]));
                if (child2 == null) {
                    child2 = new LinkedHashMap<>(); child.put(parts[i], child2);
                }
                child = child2;
            }
            child.put(parts[parts.length - 1], newValue); return map;
        } catch (Exception ex) { throw new IllegalArgumentException("failed to deepSet " + property + " - " + ex.getMessage()); }
    }

    public static void deepReplace(Object map, String property, Object newValue) {
        if (!property.contains(".")) { mapSetOrReflect(map, property, newValue); return; }
        Pair<Object, String> lastChild = lastChild(map, property);
        if (lastChild != null) { mapSetOrReflect(lastChild._a, lastChild._b, newValue); }
    }

    public static void mapSetOrReflect(Object object, String property, Object newValue) {
        if (object instanceof Map) { ((Map) object).put(property, newValue); }
        else { safeFieldValue(object, property, newValue); }
    }

    public static <T> T deepRetrieve(Object map, String property) {
        if (!property.contains(".")) { return mapGetOrReflect(map, property); }
        Pair<Object, String> lastChild = lastChild(map, property);
        return lastChild == null ? null : (T) mapGetOrReflect(lastChild._a, lastChild._b);
    }

    private static Pair<Object, String> lastChild(Object map, String property) {
        checkBool(!property.startsWith("."), "property cannot begin with a dot");
        String[] parts = property.split("\\.");
        Object child = map;
        for (int i = 0; i < parts.length - 1; i++) {
            child = mapGetOrReflect(child, parts[i]);
            if (child == null) return null;
        }
        return Pair.pair(child, parts[parts.length - 1]);
    }

    public static <T> T mapGetOrReflect(Object object, String property) {
        if (object instanceof List) {
            Matcher matcher = LIST_PATTERN.matcher(property);
            checkBool(matcher.matches(), "property `" + property + "` not formatted as a [index]");
            List list = (List) object;
            int index = Integer.parseInt(matcher.group(1));
            return index < list.size() ? (T) list.get(index) : null;
        }
        if (object instanceof Map) return (T) ((Map) object).get(property);
        Field field = declaredField(object.getClass(), property);
        return field == null ? null : (T) fieldValue(object, field);
    }

    public static <T> List<T> deepCollect(Map<String, Object> map, String property, Class<? super T> clazz) {
        checkBool(!property.startsWith("."), "property cannot begin with a dot");
        if (!property.contains(".")) {
            Object val = map.get(property);
            return val == null ? immutableList() : (val instanceof List ? (List<T>) val : immutableList((T) val));
        }
        ImmutableListBuilder<T> result = immutableListBuilder();
        walkProps(map, safePrefix(property, "."), safeSuffix(property, "."), result, clazz);
        return result.build();
    }

    private static <T> void walkProps(Map<String, Object> map, String prop, String remains, ImmutableListBuilder<T> result, Class<? super T> clazz) {
        Object child = map.get(prop);
        if (remains.isEmpty()) {
            if (child instanceof Collection) {
                for (Object gc : (Collection<?>) child) { if (isAssignable(clazz, safeClass(gc))) { result.add((T) gc); } }
            }
            else if (isAssignable(clazz, safeClass(child))) { result.add((T) child); }
            return;
        }

        Pair<String, String> remainsPair = remains.contains(".") ? pair(safePrefix(remains, "."), safeSuffix(remains, ".")) : pair(remains, "");
        if (child instanceof Map) { walkProps(asMap(child), remainsPair._a, remainsPair._b, result, clazz); }
        else if (child instanceof Collection) {
            for (Object gc : (Collection<?>) child) { walkProps(asMap(gc), remainsPair._a, remainsPair._b, result, clazz); }
        }
    }

    public static String optStr(Map<String, Object> map, String prop) { return stringVal(map, prop, false, null); }

    public static String optStr(Map<String, Object> map, String prop, String defVal) { return stringVal(map, prop, false, defVal); }

    public static String reqdStr(Map<String, Object> map, String prop) { return stringVal(map, prop, true, null); }

    public static boolean boolVal(Map<String, Object> map, String prop, boolean defVal) { return asValue(map, prop, Boolean.class, defVal); }

    public static int intVal(Map<String, Object> map, String prop, int defVal) { return asValue(map, prop, Integer.class, defVal); }

    public static long longVal(Map<String, Object> map, String prop, long defVal) { return asValue(map, prop, Long.class, defVal); }

    public static Long longStrVal(Map<String, Object> map, String prop) {
        Object value = map.get(prop);
        return value == null ? null : value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value));
    }

    public static long longStrVal(Map<String, Object> map, String prop, long defaultValue) {
        Object value = map.get(prop);
        return value == null ? defaultValue : value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value));
    }

    public static String stringVal(Map<String, Object> map, String prop, boolean required, String defVal) {
        return (String) map.getOrDefault(required ? reqdProp(map, prop) : prop, defVal);
    }

    public static String[] strArrayVal(Map<String, Object> map, String prop) { return toArray(listVal(map, prop), String.class); }

    public static <T> T[] arrayVal(Map<String, Object> map, String prop, Class<? extends T> clazz) { return toArray(listVal(map, prop), clazz); }

    public static List<Map<String, Object>> mapListVal(Map<String, Object> map, String prop) { return asValue(map, prop, List.class, immutableList()); }

    public static <T> List<T> listVal(Map<String, Object> map, String prop) { return asValue(map, prop, List.class, immutableList()); }

    public static MapResult resultVal(Map<String, Object> map, String prop) {
        Object result = map.get(prop);
        return (result instanceof MapResult) ? (MapResult) result :
            (result instanceof Map ? immutableResult(MAP_CLASS.cast(result)) : immutableResult());
    }

    public static Map<String, Object> mapVal(Map<String, Object> map, String prop) { return asValue(map, prop, MAP_CLASS, immutableMap()); }

    public static <T> T asNullable(Map<String, Object> map, String prop, Class<T> clazz) { return clazz.cast(map.getOrDefault(prop, null)); }

    public static <E extends Enum<E>> E enumValue(Map<String, Object> map, String prop, Class<E> enumClass) {
        Object obj = map.get(reqdProp(map, prop));
        return obj instanceof String ? (E) fetchEnumValueFor(enumClass, (String) obj) : enumClass.cast(obj);
    }

    public static <T> T optValue(Map<String, Object> map, String prop) { return (T) map.get(prop); }

    public static <T> T optValue(Map<String, Object> map, String prop, T defValue) { return (T) map.getOrDefault(prop, defValue); }

    public static <T> T reqdValue(Map<String, Object> map, String prop) { return (T) map.get(reqdProp(map, prop)); }

    public static <T> T asValue(Map<String, Object> map, String prop, Class<T> clazz, T defVal) { return clazz.cast(map.getOrDefault(prop, defVal)); }

    public static String reqdProp(Map<String, Object> map, String prop) { return checkContains(map, prop); }

    public static Map<String, Object> asMap(Object object) { return object == null ? immutableMap() : MAP_CLASS.cast(object); }

    public static <T> T removeVal(Map<String, Object> map, String prop) { return (T) map.remove(reqdProp(map, prop)); }

    public static <T> T removeIfExists(Map<String, Object> map, String prop) { return removeIfExists(map, prop, null); }

    public static <T> T removeIfExists(Map<String, Object> map, String prop, T defVl) { return map.containsKey(prop) ? (T) map.remove(prop) : defVl; }

    public static <K, V> Set<Map.Entry<K, V>> safeEntries(Map<K, V> map) { return map == null ? ImmutableSet.of() : map.entrySet(); }
}