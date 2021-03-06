package io.polyglotted.common.util;

import java.util.Collection;
import java.util.Map;

import static io.polyglotted.common.util.StrUtil.nullOrEmpty;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Assertions {

    public static void checkBool(boolean expression) { checkBool(expression, "failed boolean check"); }

    public static void checkBool(boolean expression, String message) { if (!expression) { throw new IllegalArgumentException(message); } }

    public static boolean isEq(Object actual, Object required, String item) { checkEq(actual, required, item); return true; }

    public static <T> T checkEq(T actual, T required, String item) {
        checkBool(actual.equals(required), "expected " + item + " equal to " + required + " but got " + actual); return actual;
    }

    public static int checkEq(int actual, int required, String item) {
        checkBool(actual == required, "expected " + item + " equal to " + required + " but got " + actual); return actual;
    }

    public static int checkGt(int actual, int required, String item) {
        checkBool(actual > required, "expected " + item + " greater than " + required + " but got " + actual); return actual;
    }

    public static int checkGte(int actual, int required, String item) {
        checkBool(actual >= required, "expected " + item + " greater than equal to " + required + " but got " + actual); return actual;
    }

    public static int checkLte(int actual, int required, String item) {
        checkBool(actual <= required, "expected " + item + " less than equal to " + required + " but got " + actual); return actual;
    }

    public static boolean checkBetween(long actual, long lower, long upper, boolean includeLower, boolean includeUpper) {
        return includeLower ? (includeUpper ? actual >= lower && actual <= upper : actual >= lower && actual < upper)
            : (includeUpper ? actual > lower && actual <= upper : actual > lower && actual < upper);
    }

    public static String checkNotNullOrEmpty(String actual, String item) {
        checkBool(!nullOrEmpty(actual), "expected " + item + " not to be empty or null"); return actual;
    }

    public static <K, V> K checkContains(Map<K, V> map, K key) { checkBool(map.containsKey(key), key + " is missing in the map"); return key; }

    public static <K, V> K checkNotContains(Map<K, V> map, K key) { checkBool(!map.containsKey(key), key + " not allowed in the map"); return key; }

    public static <E> E checkContains(Collection<E> coll, E item, String message) { checkBool(coll.contains(item), message); return item; }

    public static <E> E checkNotContains(Collection<E> coll, E item, String message) { checkBool(!coll.contains(item), message); return item; }
}