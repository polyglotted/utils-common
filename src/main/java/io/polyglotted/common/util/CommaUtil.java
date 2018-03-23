package io.polyglotted.common.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class CommaUtil {
    private static final Splitter COMMA_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final MapSplitter EQUALS_SPLITTER = COMMA_SPLITTER.withKeyValueSeparator("=");
    private static final Joiner COMMA_JOINER = Joiner.on(",").skipNulls();

    public static Map<String, String> commaEqSplit(String value) { return EQUALS_SPLITTER.split(value); }

    public static Map<String, String> mapSplit(String value, String separator) { return mapSplitter(separator).split(value); }

    public static MapSplitter mapSplitter(String keyValueSeparator) { return COMMA_SPLITTER.withKeyValueSeparator(keyValueSeparator); }

    public static List<String> commaSplit(String value) { return value == null ? null : COMMA_SPLITTER.splitToList(value); }

    public static String commaJoin(Collection<String> values) { return values == null ? null : COMMA_JOINER.join(values); }
}