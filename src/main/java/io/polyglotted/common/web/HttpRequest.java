package io.polyglotted.common.web;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import io.polyglotted.common.model.MapResult;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.polyglotted.common.util.MapRetriever.STRING_LIST_CLASS;
import static io.polyglotted.common.util.StrUtil.toLower;

@RequiredArgsConstructor
public final class HttpRequest {
    public final HttpMethod method;
    public final String uriPath;
    public final String body; //could be base64Encoded
    public final Map<String, Object> requestContext;
    public final ListMultimap<String, String> headers;
    public final ListMultimap<String, String> queryParams;

    static HttpRequest from(MapResult event) {
        return new HttpRequest(HttpMethod.valueOf(event.reqdStr("httpMethod")), event.reqdStr("path"), event.optStr("body"),
            event.mapVal("requestContext"), buildParams(event, "headers", "multiValueHeaders", true),
            buildParams(event, "queryStringParameters", "multiValueQueryStringParameters", false));
    }

    private static ListMultimap<String, String> buildParams(MapResult event, String singleParam, String multiParam, boolean lower) {
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
        for (Map.Entry<String, Object> entry : event.mapVal(singleParam).entrySet()) {
            builder.put(lower ? toLower(entry.getKey()) : entry.getKey(), (String) entry.getValue());
        }
        for (Map.Entry<String, Object> entry : event.mapVal(multiParam).entrySet()) {
            builder.putAll(lower ? toLower(entry.getKey()) : entry.getKey(), STRING_LIST_CLASS.cast(entry.getValue()));
        }
        return builder.build();
    }
}