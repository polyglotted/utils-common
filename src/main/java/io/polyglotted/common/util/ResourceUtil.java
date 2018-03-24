package io.polyglotted.common.util;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ResourceUtil {

    public static Map<String, String> readResourceAsMap(Class<?> clazz, String file) { return readResourceAsMap(clazz, file, "="); }

    @SneakyThrows
    public static Map<String, String> readResourceAsMap(Class<?> clazz, String file, String splitter) {
        Map<String, String> result = new LinkedHashMap<>();
        List<String> lines = readResourceList(clazz, file);
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) { continue; }
            int eqIndex = line.indexOf(splitter);
            result.put(line.substring(0, eqIndex), line.substring(eqIndex + 1));
        }
        return result;
    }

    public static byte[] readResourceBytes(Class<?> clazz, String resource) { return readResourceBytes(urlStream(clazz, resource)); }

    @SneakyThrows public static byte[] readResourceBytes(InputStream stream) { return ByteStreams.toByteArray(stream); }

    public static String readResource(Class<?> clazz, String resource) { return readResource(urlResource(clazz, resource)); }

    @SneakyThrows public static String readResource(URL url) { return Resources.toString(url, UTF_8); }

    public static List<String> readResourceList(Class<?> clazz, String resource) { return readResourceList(urlResource(clazz, resource)); }

    @SneakyThrows
    public static List<String> readResourceList(URL resource) { return Resources.readLines(requireNonNull(resource), UTF_8); }

    public static InputStream urlStream(Class<?> clazz, String resource) { return clazz.getClassLoader().getResourceAsStream(resource); }

    public static URL urlResource(Class<?> clazz, String resource) { return clazz.getClassLoader().getResource(resource); }

    @SneakyThrows
    public static Enumeration<URL> urlResources(Class<?> clazz, String resource) { return clazz.getClassLoader().getResources(resource); }
}