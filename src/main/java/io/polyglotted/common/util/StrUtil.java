package io.polyglotted.common.util;

import java.util.List;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class StrUtil {
    public final static String[] EMPTY_STRINGS = new String[0];

    public static boolean notNullOrEmpty(String string) { return !nullOrEmpty(string); }

    public static boolean nullOrEmpty(String string) { return string == null || string.isEmpty(); }

    public static String emptyToNull(String string) { return nullOrEmpty(string) ? null : string; }

    public static String nonNullStr(String nullable) { return requireNonNull(emptyToNull(nullable), "required String is null"); }

    public static String nonNullStr(String nullable, String checked) { return emptyToNull(nullable) != null ? nullable : checked; }

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
}