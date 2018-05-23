package io.polyglotted.common.util;

@SuppressWarnings("WeakerAccess")
public abstract class BoolUtil {

    public static boolean isTrue(String truth) { return "true".equalsIgnoreCase(truth) || "".equals(truth); }

    public static boolean isFalse(String truth) { return "false".equalsIgnoreCase(truth) || "".equals(truth); }

    public static boolean isNotFalse(String truth) { return !"false".equalsIgnoreCase(truth); }
}
