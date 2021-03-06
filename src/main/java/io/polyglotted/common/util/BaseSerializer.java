package io.polyglotted.common.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.List;
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
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static io.polyglotted.common.model.Serializers.baseModule;
import static io.polyglotted.common.util.MapRetriever.MAP_LIST_CLASS;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseSerializer {
    //COMPATIBLE WITH SPRING-BOOT
    public static final ObjectMapper MAPPER = configureMapper(new ObjectMapper().registerModule(new GuavaModule())
        .registerModule(new Jdk8Module()).registerModule(new ParameterNamesModule())).configure(ORDER_MAP_ENTRIES_BY_KEYS, true);
    public static final ObjectMapper NON_ORDERED_MAPPER = configureMapper(new ObjectMapper().registerModule(new GuavaModule())
        .registerModule(new Jdk8Module()).registerModule(new ParameterNamesModule()));
    public static final JsonFactory FACTORY = new JsonFactory(MAPPER);

    public static ObjectMapper configureMapper(ObjectMapper objectMapper) {
        return objectMapper
            .registerModule(baseModule())
            .configure(ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .configure(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .configure(ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .configure(FAIL_ON_NULL_FOR_PRIMITIVES, true)
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(READ_ENUMS_USING_TO_STRING, true)
            .configure(SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(WRITE_DATES_AS_TIMESTAMPS, true)
            .setSerializationInclusion(NON_NULL)
            .setVisibility(new VisibilityChecker.Std(NONE, NONE, NONE, ANY, ANY));
    }

    public static byte[] serializeBytes(Object object) { return serializeBytes(MAPPER, object); }

    @SneakyThrows public static byte[] serializeBytes(ObjectMapper mapper, Object object) { return mapper.writeValueAsBytes(object); }

    public static String serialize(Object object) { return serialize(MAPPER, object); }

    @SneakyThrows public static String serialize(ObjectMapper mapper, Object object) { return mapper.writeValueAsString(object); }

    public static String prettyPrint(Object object) { return prettyPrint(MAPPER, object); }

    @SneakyThrows public static String prettyPrint(ObjectMapper mapper, Object object) {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    @SneakyThrows public static <T> T deserialize(String json, Class<T> clazz) { return MAPPER.readValue(json, clazz); }

    @SneakyThrows public static <T> T deserialize(String json, TypeReference<T> ref) { return MAPPER.readValue(json, ref); }

    @SneakyThrows public static <T> T deserialize(byte[] bytes, Class<T> clazz) { return MAPPER.readValue(bytes, clazz); }

    @SneakyThrows public static <T> T deserialize(byte[] bytes, TypeReference<T> ref) { return MAPPER.readValue(bytes, ref); }

    @SneakyThrows public static <T> T deserialize(InputStream stream, Class<T> clazz) { return MAPPER.readValue(stream, clazz); }

    @SneakyThrows public static <T> T deserialize(InputStream stream, TypeReference<T> ref) { return MAPPER.readValue(stream, ref); }

    @SneakyThrows public static <T> T deserialize(Reader reader, Class<T> clazz) { return MAPPER.readValue(reader, clazz); }

    @SneakyThrows public static <T> T deserialize(Reader reader, TypeReference<T> ref) { return MAPPER.readValue(reader, ref); }

    @SneakyThrows public static List<Map<String, Object>> deserializeToList(byte[] bytes) { return MAPPER.readValue(bytes, MAP_LIST_CLASS); }

    @SneakyThrows public static List<Map<String, Object>> deserializeToList(String json) { return MAPPER.readValue(json, MAP_LIST_CLASS); }

    @SneakyThrows public static List<Map<String, Object>> deserializeToList(InputStream stream) { return MAPPER.readValue(stream, MAP_LIST_CLASS); }

    @SneakyThrows public static List<Map<String, Object>> deserializeToList(Reader reader) { return MAPPER.readValue(reader, MAP_LIST_CLASS); }

    @SneakyThrows public static MapResult deserialize(byte[] bytes) { return MAPPER.readValue(bytes, SimpleMapResult.class); }

    @SneakyThrows public static MapResult deserialize(String json) { return MAPPER.readValue(json, SimpleMapResult.class); }

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