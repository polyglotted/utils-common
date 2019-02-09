package io.polyglotted.common.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.impl.EnglishReasonPhraseCatalog;

import java.io.OutputStream;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static io.polyglotted.common.util.BaseSerializer.MAPPER;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.MapBuilder.immutableMultimap;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

@SuppressWarnings("WeakerAccess") @JsonAutoDetect @RequiredArgsConstructor
@JsonPropertyOrder({"statusCode", "statusDescription", "headers", "multiValueHeaders", "isBase64Encoded", "body"})
public class GatewayResponse {
    public final int statusCode;
    public final String statusDescription;
    public final boolean isBase64Encoded;
    public final String body;
    @JsonInclude(NON_EMPTY) public final Map<String, String> headers;
    @JsonInclude(NON_EMPTY) public final Multimap<String, String> multiValueHeaders;

    public static void sendError(boolean isLoadBalanced, OutputStream output, Exception ex) {
        sendResult(isLoadBalanced, output, ex instanceof WebException ? ((WebException) ex).httpStatus : SC_INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    public static void sendResult(boolean isLoadBalanced, OutputStream output, int httpStatus, String result) {
        sendResult(isLoadBalanced, output, httpStatus, result, false, immutableMap(CONTENT_TYPE, "application/json"));
    }

    @SneakyThrows public static void sendResult(boolean isLoadBalanced, OutputStream output, int status, String result,
                                                boolean isBase64, Map<String, String> headers) {
        String desc = isLoadBalanced ? status + " " + EnglishReasonPhraseCatalog.INSTANCE.getReason(status, null) : null;
        ImmutableMultimap<String, String> multiHeaders = isLoadBalanced ? immutableMultimap(headers) : immutableMultimap();
        MAPPER.writeValue(output, new GatewayResponse(status, desc, isBase64, result, headers, multiHeaders));
    }
}