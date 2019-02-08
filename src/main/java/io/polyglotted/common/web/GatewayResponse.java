package io.polyglotted.common.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@SuppressWarnings("WeakerAccess") @JsonAutoDetect
@RequiredArgsConstructor
public class GatewayResponse<T> {
    public final boolean isBase64Encoded;
    public final int statusCode;
    public final Map<String, String> headers;
    public final T body;
}