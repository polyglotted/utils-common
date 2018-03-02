package io.polyglotted.common.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.polyglotted.common.model.Pair;

import static com.google.common.collect.Iterables.getFirst;
import static io.polyglotted.common.model.Pair.pair;
import static io.polyglotted.common.util.NullUtil.nonNull;
import static io.polyglotted.common.util.StrUtil.notNullOrEmpty;
import static io.polyglotted.common.util.StrUtil.safePrefix;
import static io.polyglotted.common.util.StrUtil.safeSuffix;

public abstract class UrnUtil {
    private static final String COLON = ":";
    private static final Joiner COLON_JOINER = Joiner.on(COLON).skipNulls();
    private static final Splitter COLON_SPLITTER = Splitter.on(COLON);

    public static String urnOf(String a, String b) { return notNullOrEmpty(a) ? (notNullOrEmpty(b) ? a + COLON + b : a) : (notNullOrEmpty(b) ? b : ""); }

    public static String safeUrnOf(String... parts) { return COLON_JOINER.join(parts); }

    public static Pair<String, String> urnSplit(String urn) { return pair(safePrefix(urn, COLON), safeSuffix(urn, COLON)); }

    public static String first(String urn) { return nonNull(urn, "").contains(COLON) ? getFirst(COLON_SPLITTER.split(urn), null) : ""; }
}