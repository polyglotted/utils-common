package io.polyglotted.common.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static java.util.Objects.requireNonNull;

@EqualsAndHashCode
@RequiredArgsConstructor
public final class GeoShape {
    public final GeoType type;
    public final String coordinates;
    public final String radius;

    @SuppressWarnings("unused") GeoShape() { this(GeoType.point, "0.0", null); }

    public static Builder shapeBuilder() { return new Builder(); }

    @Setter @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder implements io.polyglotted.common.model.Builder<GeoShape> {
        private GeoType type;
        private String coordinates;
        private String radius;

        @Override public GeoShape build() {
            return new GeoShape(requireNonNull(type, "type is required"), requireNonNull(coordinates, "coordinates is required"), radius);
        }
    }
}