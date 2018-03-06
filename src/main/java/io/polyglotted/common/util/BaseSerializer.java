package io.polyglotted.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.polyglotted.common.model.MapResult;
import io.polyglotted.common.model.MapResult.SimpleMapResult;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY;
import static com.fasterxml.jackson.databind.DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_ENUMS_USING_TO_STRING;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static io.polyglotted.common.model.Serializers.baseModule;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseSerializer {
    private static final ObjectMapper MAPPER = buildMapper();

    private static ObjectMapper buildMapper() {
        return new ObjectMapper()
            .registerModule(baseModule()).registerModule(new GuavaModule()).registerModule(new Jdk8Module()).registerModule(new ParameterNamesModule())
            .configure(READ_ENUMS_USING_TO_STRING, true).configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(FAIL_ON_NULL_FOR_PRIMITIVES, true).configure(ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true).configure(ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .configure(ORDER_MAP_ENTRIES_BY_KEYS, true).configure(SORT_PROPERTIES_ALPHABETICALLY, true)
            .setSerializationInclusion(NON_NULL).setVisibility(new VisibilityChecker.Std(NONE, NONE, NONE, ANY, ANY));
    }

    @SneakyThrows public static byte[] serializeBytes(Object object) { return serializeBytes(MAPPER, object); }

    @SneakyThrows public static byte[] serializeBytes(ObjectMapper mapper, Object object) { return mapper.writeValueAsBytes(object); }

    @SneakyThrows public static String serialize(Object object) { return serialize(MAPPER, object); }

    @SneakyThrows public static String serialize(ObjectMapper mapper, Object object) { return mapper.writeValueAsString(object); }

    @SneakyThrows public static <T> T deserialize(String json, Class<T> clazz) { return deserialize(MAPPER, json, clazz); }

    @SneakyThrows public static <T> T deserialize(ObjectMapper mapper, String json, Class<T> clazz) { return mapper.readValue(json, clazz); }

    @SneakyThrows public static <T> T deserialize(byte[] bytes, Class<T> clazz) { return deserialize(MAPPER, bytes, clazz); }

    @SneakyThrows public static <T> T deserialize(ObjectMapper mapper, byte[] bytes, Class<T> clazz) { return mapper.readValue(bytes, clazz); }

    @SneakyThrows public static <T> T deserialize(InputStream stream, Class<T> clazz) { return deserialize(MAPPER, stream, clazz); }

    @SneakyThrows public static <T> T deserialize(ObjectMapper mapper, InputStream stream, Class<T> clazz) { return mapper.readValue(stream, clazz); }

    @SneakyThrows public static <T> T deserialize(Reader reader, Class<T> clazz) { return deserialize(MAPPER, reader, clazz); }

    @SneakyThrows public static <T> T deserialize(ObjectMapper mapper, Reader reader, Class<T> clazz) { return mapper.readValue(reader, clazz); }

    @SneakyThrows public static MapResult deserialize(byte[] bytes) { return deserialize(MAPPER, bytes); }

    @SneakyThrows public static MapResult deserialize(ObjectMapper mapper, byte[] bytes) { return deserialize(mapper, bytes, SimpleMapResult.class); }

    @SneakyThrows public static MapResult deserialize(String json) { return deserialize(MAPPER, json); }

    @SneakyThrows public static MapResult deserialize(ObjectMapper mapper, String json) { return deserialize(mapper, json, SimpleMapResult.class); }

    public static void writeNotEmptyMap(JsonGenerator gen, String name, Map<?, ?> map) throws IOException {
        if (!map.isEmpty()) { gen.writeObjectField(name, map); }
    }

    public static void writeNotEmptyColl(JsonGenerator gen, String name, Collection<?> coll) throws IOException {
        if (!coll.isEmpty()) { gen.writeObjectField(name, coll); }
    }

    public static void writeNotNull(JsonGenerator gen, String name, Object obj) throws IOException {
        if (obj != null) { gen.writeObjectField(name, obj); }
    }

    public static void writeNotNullOrEmpty(JsonGenerator gen, String name, Object obj) throws IOException {
        if (obj instanceof Map) { writeNotEmptyMap(gen, name, (Map<?, ?>) obj); }
        else if (obj instanceof Collection) { writeNotEmptyColl(gen, name, (Collection<?>) obj); }
        else if (obj != null) { gen.writeObjectField(name, obj); }
    }
}