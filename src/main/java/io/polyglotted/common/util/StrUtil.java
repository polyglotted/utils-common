package io.polyglotted.common.util;

import java.util.List;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class StrUtil {
    public final static String[] EMPTY_STRINGS = new String[0];

    public static boolean notNullOrEmpty(String string) { return !nullOrEmpty(string); }

    public static boolean nullOrEmpty(String string) { return string == null || string.isEmpty(); }

    public static String emptyAsNull(String string) { return nullOrEmpty(string) ? null : string; }

    public static String nullAsEmpty(String string) { return nullOrEmpty(string) ? "" : string; }

    public static String nullOrStr(Object object) { return object == null ? null : String.valueOf(object); }

    public static String toLower(Object object) { return object == null ? "" : String.valueOf(object).toLowerCase(); }

    public static String nonNullStr(String nullable) { return requireNonNull(emptyAsNull(nullable), "required String is null"); }

    public static String nonNullStr(String nullable, String checked) { return emptyAsNull(nullable) != null ? nullable : checked; }

    public static String safePrefix(String word, String delimiter) { return safePrefix(word, delimiter, ""); }

    public static String safePrefix(String word, String delimiter, String defValue) {
        return word.contains(delimiter) ? word.substring(0, word.indexOf(delimiter)) : defValue;
    }

    public static String safeSuffix(String word, String delimiter) { return word.substring(word.indexOf(delimiter) + delimiter.length()); }

    public static String safeLastPrefix(String word, String delimiter) {
        return word.contains(delimiter) ? word.substring(0, word.lastIndexOf(delimiter)) : "";
    }

    public static String safeLastSuffix(String word, String delimiter) {
        return word.contains(delimiter) ? word.substring(word.lastIndexOf(delimiter) + delimiter.length()) : "";
    }

    public static String stringOf(List<String> strings) { String result = String.valueOf(strings); return result.substring(1, result.length() - 1); }

    public static String[] toStrArray(Iterable<String> strings) { return CollUtil.toArray(strings, String.class); }
}