package io.polyglotted.common.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@SuppressWarnings({"unused", "WeakerAccess"})
@RequiredArgsConstructor(access = PRIVATE)
public final class Nil {

    @JsonValue public Object jsonValue() {
        return null;
    }

    public static final Nil NIL_OBJECT = new Nil();

    public static boolean isNil(Object input) { return input == null || input == NIL_OBJECT; }

    public static Object asNil(Object input) { return (input == null) ? NIL_OBJECT : input; }

    public static Object nilAsNull(Object input) { return NIL_OBJECT.equals(input) ? null : input; }
}