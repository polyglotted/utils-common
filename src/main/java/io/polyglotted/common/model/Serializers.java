package io.polyglotted.common.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.polyglotted.common.model.MapResult.SimpleMapResult;
import io.polyglotted.common.util.Sanitizer;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.polyglotted.common.util.DateFormatters.parseDateTime;

public abstract class Serializers {
    public static SimpleModule baseModule() {
        SimpleModule module = new SimpleModule("BaseSerializer");
        module.addAbstractTypeMapping(MapResult.class, SimpleMapResult.class);
        module.addSerializer(Double.TYPE, new DoubleSerializer());
        module.addSerializer(GeoPoint.class, new ToStringSerializer<>());
        module.addSerializer(InetAddress.class, new IpSerializer());
        module.addSerializer(LocalDate.class, new ToStringSerializer<>());
        module.addSerializer(LocalDateTime.class, new ToStringSerializer<>());
        module.addSerializer(LocalTime.class, new ToStringSerializer<>());
        module.addSerializer(OffsetDateTime.class, new ToStringSerializer<>());
        module.addSerializer(OffsetTime.class, new ToStringSerializer<>());
        module.addSerializer(ZonedDateTime.class, new ToStringSerializer<>());
        module.addDeserializer(LocalDate.class, new SanitizeSerializer<>(LocalDate.class));
        module.addDeserializer(LocalDateTime.class, new SanitizeSerializer<>(LocalDateTime.class));
        module.addDeserializer(LocalTime.class, new SanitizeSerializer<>(LocalTime.class));
        module.addDeserializer(Long.TYPE, new DateLongSerializer(Long.TYPE, 0L));
        module.addDeserializer(Long.class, new DateLongSerializer(Long.class, null));
        module.addDeserializer(OffsetDateTime.class, new SanitizeSerializer<>(OffsetDateTime.class));
        module.addDeserializer(OffsetTime.class, new SanitizeSerializer<>(OffsetTime.class));
        module.addDeserializer(String.class, new StdStringSerializer());
        module.addDeserializer(ZonedDateTime.class, new SanitizeSerializer<>(ZonedDateTime.class));
        return module;
    }

    private static class DoubleSerializer extends JsonSerializer<Double> {
        @Override
        public void serialize(Double src, JsonGenerator gen, SerializerProvider prov) throws IOException {
            if (src == src.longValue()) { gen.writeNumber(src.longValue()); }
            else { gen.writeNumber(src); }
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

    private static class ToStringSerializer<T> extends JsonSerializer<T> {
        @Override
        public void serialize(T src, JsonGenerator gen, SerializerProvider prov) throws IOException {
            if (src != null) { gen.writeString(src.toString()); }
        }
    }

    public static class IpSerializer extends JsonSerializer<InetAddress> {
        @Override
        public void serialize(InetAddress src, JsonGenerator gen, SerializerProvider prov) throws IOException {
            if (src != null) { gen.writeString(src.getHostAddress()); }
        }
    }

    private static class DateLongSerializer extends StdDeserializer<Long> {
        private final NumberDeserializers.LongDeserializer backoff;

        DateLongSerializer(Class<Long> vc, Long nvl) { super(vc); backoff = new NumberDeserializers.LongDeserializer(vc, nvl); }

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