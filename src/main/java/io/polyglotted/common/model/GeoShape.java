package io.polyglotted.common.model;

import lombok.*;
import lombok.experimental.Accessors;

import static com.google.common.base.Preconditions.checkNotNull;

@ToString(includeFieldNames = false, doNotUseGetters = true)
@EqualsAndHashCode
@RequiredArgsConstructor
public final class GeoShape {
    public final GeoType type;
    public final String coordinates;
    public final String radius;

    @SuppressWarnings("unused") GeoShape() { this(GeoType.point, "0.0", null); }

    public static Builder shapeBuilder() { return new Builder(); }

    @Setter
    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private GeoType type;
        private String coordinates;
        private String radius;

        public GeoShape build() {
            return new GeoShape(checkNotNull(type, "type is required"), checkNotNull(coordinates, "coordinates is required"), radius);
        }
    }
}