package io.polyglotted.common.web;

import io.polyglotted.common.model.MapResult.SimpleMapResult;
import io.polyglotted.common.model.Pair;
import io.polyglotted.common.util.MapBuilder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.polyglotted.common.util.StrUtil.nullOrStr;
import static io.polyglotted.common.util.StrUtil.toLower;

@RequiredArgsConstructor
public final class HttpRequest {
    public final HttpMethod method;
    public final String uriPath;
    public final String body; //could be base64Encoded
    public final Map<String, Object> requestContext;
    public final Map<String, Object> headers;
    public final Map<String, Object> queryParams;

    static HttpRequest from(SimpleMapResult event) {
        return new HttpRequest(HttpMethod.valueOf(event.reqdStr("httpMethod")), event.reqdStr("path"), event.optStr("body"),
            event.mapVal("requestContext"), lowerCaseKeys(event.mapVal("headers")), event.mapVal("queryStringParameters"));
    }

    private static Map<String, Object> lowerCaseKeys(Map<String, Object> input) {
        return MapBuilder.<String, Object>immutableMapBuilder()
            .putTransformed(input, e -> Pair.pair(toLower(e.getKey()), nullOrStr(e.getValue()))).build();
    }
}