package io.polyglotted.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.polyglotted.common.util.Assertions.checkBool;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class GeoPoint {
    private final static double EPSILON = 0.0000001;
    private static final Pattern GEO_JSON = Pattern.compile("([-+]?[0-9]*\\.?[0-9]+)");
    public final double lat;
    public final double lon;

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && doubleEquals(lat, ((GeoPoint) o).lat) && doubleEquals(lon, ((GeoPoint) o).lon);
    }

    @Override
    public int hashCode() { return Objects.hash(lat, lon); }

    @Override
    public String toString() { return "[" + lon + "," + lat + "]"; }

    @JsonCreator public static GeoPoint geoPointFromString(String value) {
        Matcher matcher = GEO_JSON.matcher(value);
        checkBool(matcher.find(), "cannot find longitude");
        Double longitude = Double.parseDouble(matcher.group());
        checkBool(matcher.find(), "cannot find longitude");
        Double latitude = Double.parseDouble(matcher.group());
        checkBool(!matcher.find(), "no match found");

        return new GeoPoint(latitude, longitude);
    }

    private static boolean doubleEquals(double a, double b) { return a == b || Math.abs(a - b) < EPSILON; }
}