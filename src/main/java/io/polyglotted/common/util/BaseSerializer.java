package io.polyglotted.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers.LongDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import io.polyglotted.common.model.GeoPoint;
import io.polyglotted.common.model.MapResult;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.google.common.base.Strings.isNullOrEmpty;
import static io.polyglotted.common.model.MapResult.simpleResult;
import static io.polyglotted.common.util.DateFormatters.parseDateTime;
import static io.polyglotted.common.util.MapRetriever.MAP_CLASS;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseSerializer {
    public static final ObjectMapper MAPPER = buildMapper();
    public static final ObjectMapper ORDERED_MAPPER = buildMapper().configure(ORDER_MAP_ENTRIES_BY_KEYS, true);

    private static ObjectMapper buildMapper() {
        return new ObjectMapper()
            .registerModule(baseModule()).registerModule(new GuavaModule())
            .configure(READ_ENUMS_USING_TO_STRING, true).configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(FAIL_ON_NULL_FOR_PRIMITIVES, true).configure(ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true).configure(ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .setSerializationInclusion(NON_NULL).setSerializationInclusion(NON_EMPTY)
            .setVisibility(new VisibilityChecker.Std(NONE, NONE, NONE, ANY, ANY));
    }

    @SneakyThrows public static <T> byte[] serializeBytes(T object) { return MAPPER.writeValueAsBytes(object); }

    @SneakyThrows public static String serialize(Object object) { return MAPPER.writeValueAsString(object); }

    @SneakyThrows public static <T> T deserialize(String json, Class<T> clazz) { return MAPPER.readValue(json, clazz); }

    @SneakyThrows public static <T> T deserialize(byte[] bytes, Class<T> clazz) { return MAPPER.readValue(bytes, clazz); }

    @SneakyThrows public static <T> T deserialize(InputStream stream, Class<T> clazz) { return MAPPER.readValue(stream, clazz); }

    @SneakyThrows public static <T> T deserialize(Reader reader, Class<T> clazz) { return MAPPER.readValue(reader, clazz); }

    @SneakyThrows public static MapResult deserialize(byte[] bytes) { return simpleResult(deserialize(bytes, MAP_CLASS)); }

    @SneakyThrows public static MapResult deserialize(String json) { return simpleResult(deserialize(json, MAP_CLASS)); }

    public static void writeNotEmptyMap(JsonGenerator gen, String name, Map<?, ?> map) throws IOException {
        if (!map.isEmpty()) { gen.writeObjectField(name, map); }
    }

    public static void writeNotEmptyColl(JsonGenerator gen, String name, Collection<?> coll) throws IOException {
        if (!coll.isEmpty()) { gen.writeObjectField(name, coll); }
    }

    public static void writeNotNull(JsonGenerator gen, String name, Object obj) throws IOException {
        if (obj != null) { gen.writeObjectField(name, obj); }
    }

    @SuppressWarnings("unused") public static void writeNotNullOrEmpty(JsonGenerator gen, String name, Object obj) throws IOException {
        if (obj instanceof Map) { writeNotEmptyMap(gen, name, (Map<?, ?>) obj); }
        else if (obj instanceof Collection) { writeNotEmptyColl(gen, name, (Collection<?>) obj); }
        else if (obj != null) { gen.writeObjectField(name, obj); }
    }

    private static SimpleModule baseModule() {
        SimpleModule module = new SimpleModule("BaseSerializer");
        module.addSerializer(Double.TYPE, new DoubleSerializer());
        module.addSerializer(LocalDate.class, new ToStringSerializer<>());
        module.addSerializer(LocalTime.class, new ToStringSerializer<>());
        module.addSerializer(OffsetTime.class, new ToStringSerializer<>());
        module.addSerializer(ZonedDateTime.class, new ToStringSerializer<>());
        module.addSerializer(OffsetDateTime.class, new ToStringSerializer<>());
        module.addSerializer(LocalDateTime.class, new ToStringSerializer<>());
        module.addSerializer(UUID.class, new ToStringSerializer<>());
        module.addSerializer(GeoPoint.class, new ToStringSerializer<>());
        module.addSerializer(InetAddress.class, new IpSerializer());
        module.addDeserializer(String.class, new StdStringSerializer());
        module.addDeserializer(Long.TYPE, new DateLongSerializer(Long.TYPE, 0L));
        module.addDeserializer(Long.class, new DateLongSerializer(Long.class, null));
        module.addDeserializer(LocalDate.class, new SanitizeSerializer<>(LocalDate.class));
        module.addDeserializer(LocalTime.class, new SanitizeSerializer<>(LocalTime.class));
        module.addDeserializer(OffsetTime.class, new SanitizeSerializer<>(OffsetTime.class));
        module.addDeserializer(ZonedDateTime.class, new SanitizeSerializer<>(ZonedDateTime.class));
        module.addDeserializer(OffsetDateTime.class, new SanitizeSerializer<>(OffsetDateTime.class));
        module.addDeserializer(LocalDateTime.class, new SanitizeSerializer<>(LocalDateTime.class));
        return module;
    }

    private static class DoubleSerializer extends JsonSerializer<Double> {
        @Override
        public void serialize(Double src, JsonGenerator gen, SerializerProvider prov) throws IOException {
            if (src == src.longValue()) { gen.writeNumber(src.longValue()); }
            else { gen.writeNumber(src); }
        }
    }

    private static class ToStringSerializer<T> extends JsonSerializer<T> {
        @Override
        public void serialize(T src, JsonGenerator gen, SerializerProvider prov) throws IOException {
            if (src != null) { gen.writeString(src.toString()); }
        }
    }

    private static class IpSerializer extends JsonSerializer<InetAddress> {
        @Override
        public void serialize(InetAddress src, JsonGenerator gen, SerializerProvider prov) throws IOException {
            if (src != null) { gen.writeString(src.getHostAddress()); }
        }
    }

    private static class StdStringSerializer extends StdDeserializer<String> {
        StdStringSerializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String result = StringDeserializer.instance.deserialize(p, ctxt);
            return isNullOrEmpty(result) ? null : result;
        }
    }

    private static class DateLongSerializer extends StdDeserializer<Long> {
        private final LongDeserializer backoff;

        DateLongSerializer(Class<Long> vc, Long nvl) { super(vc); backoff = new LongDeserializer(vc, nvl); }

        @Override public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentTokenId() == JsonTokenId.ID_STRING) {
                String value = p.getText().trim();
                if (value.length() > 0) {
                    try { return parseDateTime(value).toInstant().toEpochMilli(); } catch (DateTimeParseException ignored) { }
                }
            }
            return backoff.deserialize(p, ctxt);
        }
    }

    private static class SanitizeSerializer<T> extends StdDeserializer<T> {
        SanitizeSerializer(Class<T> clazz) { super(clazz); }

        @Override @SuppressWarnings("unchecked")
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String result = StringDeserializer.instance.deserialize(p, ctxt);
            return isNullOrEmpty(result) ? null : (T) Sanitizer.sanitize(_valueClass, result);
        }
    }
}