package io.polyglotted.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Pattern;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_TIME;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.TemporalQueries.localTime;
import static java.time.temporal.TemporalQueries.zone;

@SuppressWarnings("WeakerAccess")
public abstract class DateFormatters {
    private static final Pattern NUMBER = Pattern.compile("\\d*");
    private static final String[] DATE_TIME_FORMATS = {
        "yyyyMMdd'T'HHmm[ss]",
        "yyyyMMdd[ HHmm[ss]]",
        "yyyy-MM-dd[ HH:mm[:ss]]",
        "yyyy/MM/dd[ HH:mm[:ss]]",
        "MM/dd/yyyy[ HH:mm[:ss]]",
        "dd-MM-yyyy[ HH:mm[:ss]]",
        "dd MM yyyy[ HH:mm[:ss]]",
        "dd MMM yyyy[ HH:mm[:ss]]",
        "dd MMMM yyyy[ HH:mm[:ss]]",
    };
    private static final DateTimeFormatter DATE_TIME_FORMATTER = createDateTimeFormatter();

    public static LocalDate parseDate(String text) {
        return (NUMBER.matcher(text).matches()) ? toInst(text).atOffset(UTC).toLocalDate() : DATE_TIME_FORMATTER.parse(text, LocalDate::from);
    }

    public static LocalTime parseTime(String text) { return LocalTime.parse(text, ISO_TIME); }

    public static ZonedDateTime parseDateTime(String text) {
        if (NUMBER.matcher(text).matches()) return ZonedDateTime.ofInstant(toInst(text), UTC);
        TemporalAccessor parse = DATE_TIME_FORMATTER.parse(text);
        return (parse.query(zone()) != null) ? parse.query(ZonedDateTime::from) : ((parse.query(localTime()) != null) ?
            parse.query(LocalDateTime::from).atZone(UTC) : parse.query(LocalDate::from).atTime(0, 0).atZone(UTC));
    }

    public static String maybeParseDateTime(Object value) {
        try {
            return timeValueOf(DATE_TIME_FORMATTER.parseBest(String.valueOf(value), ZonedDateTime::from, LocalDateTime::from, LocalDate::from));
        } catch (Exception ex) { return timeValueOf(value); }
    }

    private static String timeValueOf(Object accessor) {
        if (accessor instanceof ZonedDateTime) { return String.valueOf(((ZonedDateTime) accessor).toInstant().toEpochMilli()); }
        else if (accessor instanceof LocalDateTime) { return String.valueOf(((LocalDateTime) accessor).toInstant(UTC).toEpochMilli()); }
        else if (accessor instanceof LocalDate) { return String.valueOf(((LocalDate) accessor).atStartOfDay().toInstant(UTC).toEpochMilli()); }
        else if (accessor instanceof LocalTime) { return String.valueOf(((LocalTime) accessor).toNanoOfDay()); }
        else return String.valueOf(accessor);
    }

    private static DateTimeFormatter createDateTimeFormatter() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.optionalStart().append(ISO_DATE_TIME).optionalEnd();
        for (String pattern : DATE_TIME_FORMATS) {
            builder.optionalStart().appendPattern(pattern).appendFraction(NANO_OF_SECOND, 0, 9, true).optionalEnd();
        }
        return builder.toFormatter();
    }

    private static Instant toInst(String text) { return Instant.ofEpochMilli(Long.parseLong(text)); }
}