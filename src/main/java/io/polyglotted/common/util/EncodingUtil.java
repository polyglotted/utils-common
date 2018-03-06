package io.polyglotted.common.util;

import io.polyglotted.common.model.Pair;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

import static io.polyglotted.common.model.Pair.pair;
import static io.polyglotted.common.util.StrUtil.safePrefix;
import static io.polyglotted.common.util.StrUtil.safeSuffix;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class EncodingUtil {

    public static String encodeBase64(byte[] bytes) { return Base64.getEncoder().encodeToString(bytes); }

    public static byte[] decodeBase64(String value) { return Base64.getDecoder().decode(value); }

    @SneakyThrows public static String urlEncode(String path) { return URLEncoder.encode(path, "utf-8"); }

    @SneakyThrows public static String urlDecode(String path) { return URLDecoder.decode(path, "utf-8"); }

    public static String uriEncode(String uri, String path) { return safeSuffix(URI.create(uri).normalize().getPath(), path); }

    public static Pair<String, String> uriSuffixPair(String uri, String path) {
        String fullSuffix = uriEncode(uri, path); return pair(safePrefix(fullSuffix, "/"), safeSuffix(fullSuffix, "/"));
    }
}