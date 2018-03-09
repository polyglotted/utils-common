package io.polyglotted.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import io.polyglotted.common.model.MapResult;
import io.polyglotted.common.model.Pair;
import io.polyglotted.common.util.ListBuilder.ImmutableListBuilder;
import io.polyglotted.common.util.MapBuilder.ImmutableMapBuilder;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.transform;
import static io.polyglotted.common.util.EnumCache.fetchEnumValueFor;
import static io.polyglotted.common.util.MapBuilder.immutableMapBuilder;
import static io.polyglotted.common.util.NullUtil.nonNullFn;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;
import static java.lang.reflect.Modifier.isVolatile;
import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ReflectionUtil {
    private static final Map<Class<?>, Object> JAVA_DEFAULTS = ImmutableMap.<Class<?>, Object>builder()
        .put(Boolean.TYPE, false).put(Character.TYPE, Character.MIN_VALUE).put(Byte.TYPE, Byte.valueOf("0")).put(Short.TYPE, Short.valueOf("0"))
        .put(Integer.TYPE, 0).put(Long.TYPE, Long.valueOf("0")).put(Float.TYPE, Float.valueOf("0.0")).put(Double.TYPE, 0.0).build();

    public static Class<?> safeClass(Object object) { return nonNullFn(object, Object::getClass, Void.class); }

    public static Class<?> safeForName(String className) {
        try {
            return className == null ? null : Class.forName(className);
        } catch (ClassNotFoundException cfe) {
            return null;
        }
    }

    @SneakyThrows
    public static Object create(Class<?> clazz) {
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        return (constructor.getParameterCount() == 0) ?
            constructor.newInstance() : initWithArgs(constructor);
    }

    public static Object initWithArgs(Constructor<?> constructor) throws Exception {
        Class<?>[] params = constructor.getParameterTypes();
        Object[] initargs = new Object[params.length];
        for (int i = 0; i < params.length; i++)
            initargs[i] = JAVA_DEFAULTS.get(params[i]);
        return constructor.newInstance(initargs);
    }

    public static Object asEnum(Class<?> clazz, String value) { return isEnum(clazz) ? fetchEnumValueFor(clazz, value) : value; }

    public static boolean isEnum(Class<?> clazz) { return requireNonNull(clazz, "null enum class").isEnum() || Enum.class.isAssignableFrom(clazz); }

    public static boolean isAssignable(Class<?> from, Class<?> to) { return to != null && from.isAssignableFrom(to); }

    public static <T> T safeFieldValue(Object object, String fieldName) {
        Field field = declaredField(object.getClass(), fieldName); return (field == null) ? null : fieldValue(object, field);
    }

    public static <T> T fieldValue(Object object, String fieldName) { return fieldValue(object, declaredField(object.getClass(), fieldName)); }

    @SuppressWarnings("unchecked") public static <T> T fieldValue(Object object, Field field) {
        try {
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            throw new IllegalStateException("unable to find field value for " + field, e);
        }
    }

    public static <T> T fieldValue(T object, String fieldName, Object value) {
        return fieldValue(object, declaredField(object.getClass(), fieldName), value);
    }

    public static <T> T fieldValue(T object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
            return object;
        } catch (Exception e) {
            throw new IllegalStateException("unable to set field value for " + field, e);
        }
    }

    public static Field declaredField(Class<?> clazz, String name) {
        Field result = null;
        while (clazz != Object.class) {
            try {
                result = clazz.getDeclaredField(name);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return result;
    }

    public static MapResult fieldValues(Object object) {
        ImmutableMapBuilder<String, Object> builder = immutableMapBuilder(ImmutableSortedMap::naturalOrder);
        for (Field field : declaredFields(object.getClass())) {
            if (isFieldSerializable(field)) {
                Object value = fieldValue(object, field);
                if (value != null) builder.put(field.getName(), value);
            }
        }
        return builder.result();
    }

    public static List<Field> declaredFields(Class<?> clazz) {
        ImmutableListBuilder<Field> result = ListBuilder.immutableListBuilder();
        while (clazz != null && !Object.class.equals(clazz)) {
            for (Field field : clazz.getDeclaredFields()) { result.add(field); }
            clazz = clazz.getSuperclass();
        }
        return result.build();
    }

    public static boolean isFieldSerializable(Field field) {
        int modifiers = field.getModifiers();
        return !(isStatic(modifiers) || isTransient(modifiers) || isVolatile(modifiers));
    }

    public static <T> T safeInvoke(Object object, String methodName, Object... params) {
        if (object == null) return null;
        Class<?>[] paramClasses = transform(copyOf(params), Object::getClass).toArray(new Class[0]);
        return safeInvoke(object.getClass(), object, methodName, paramClasses, params);
    }

    @SneakyThrows @SuppressWarnings("unchecked")
    public static <T> T safeInvoke(Class<?> clazz, Object object, String methodName, Class<?>[] paramClasses, Object... params) {
        try {
            Method method = clazz.getMethod(methodName, paramClasses);
            method.setAccessible(true);
            return (T) method.invoke(object, params);

        } catch (Exception e) { throw e.getCause() != null ? e.getCause() : e; }
    }

    public static Class<?> getCollTypeArg(Field field) { return getTypeArg(getFieldTypeArgs(field)[0]); }

    public static Pair<Class<?>, Class<?>> getMapTypeArgs(Field field) {
        Type[] fieldTypeArgs = getFieldTypeArgs(field); return Pair.pair(getTypeArg(fieldTypeArgs[0]), getTypeArg(fieldTypeArgs[1]));
    }

    public static Type[] getFieldTypeArgs(Field field) { return ((ParameterizedType) field.getGenericType()).getActualTypeArguments(); }

    public static Class<?> getTypeArg(Type typeArg) { return (typeArg instanceof Class<?>) ? (Class<?>) typeArg : Object.class; }
}