package io.polyglotted.common.util;

import com.google.common.io.CharStreams;
import io.polyglotted.common.model.MapResult;
import io.polyglotted.common.util.HttpRequestBuilder.HttpReqType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.io.InputStreamReader;

import static io.polyglotted.common.model.MapResult.simpleResult;
import static io.polyglotted.common.util.Assertions.checkBetween;
import static io.polyglotted.common.util.BaseSerializer.deserialize;
import static io.polyglotted.common.util.HttpRequestBuilder.HttpReqType.DELETE;
import static io.polyglotted.common.util.HttpRequestBuilder.HttpReqType.GET;
import static io.polyglotted.common.util.HttpRequestBuilder.HttpReqType.POST;
import static io.polyglotted.common.util.HttpRequestBuilder.HttpReqType.PUT;
import static io.polyglotted.common.util.MapRetriever.MAP_CLASS;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpStatus.SC_MULTIPLE_CHOICES;
import static org.apache.http.HttpStatus.SC_OK;

@Slf4j
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class HttpUtil {
    public static HttpRequestBuilder buildGet(String uri) { return buildGet(uri, null); }

    public static HttpRequestBuilder buildGet(String uri, String path) { return buildRequest(GET, uri, path); }

    public static HttpRequestBuilder buildPost(String uri) { return buildPost(uri, null); }

    public static HttpRequestBuilder buildPost(String uri, String path) { return buildRequest(POST, uri, path); }

    public static HttpRequestBuilder buildPut(String uri) { return buildPut(uri, null); }

    public static HttpRequestBuilder buildPut(String uri, String path) { return buildRequest(PUT, uri, path); }

    public static HttpRequestBuilder buildDelete(String uri) { return buildDelete(uri, null); }

    public static HttpRequestBuilder buildDelete(String uri, String path) { return buildRequest(DELETE, uri, path); }

    public static HttpRequestBuilder buildRequest(HttpReqType type, String uri, String path) { return new HttpRequestBuilder(type, uri, path); }

    public static MapResult execute(HttpClient client, HttpRequestBuilder builder) { return execute(client, builder.request()); }

    @SneakyThrows public static <H extends HttpRequestBase> MapResult execute(HttpClient client, H post) {
        return simpleResult(execute(client, post, MAP_CLASS));
    }

    public static <R> R execute(HttpClient client, HttpRequestBuilder builder, Class<R> clazz) { return execute(client, builder.request(), clazz); }

    @SneakyThrows public static <H extends HttpRequestBase, R> R execute(HttpClient client, H post, Class<R> clazz) {
        try {
            return deserialize(executeRaw(client, post).getEntity().getContent(), clazz);
        } finally { post.releaseConnection(); }
    }

    public static String executePlain(HttpClient client, HttpRequestBuilder builder) { return executePlain(client, builder.request()); }

    @SneakyThrows public static <H extends HttpRequestBase> String executePlain(HttpClient client, H post) {
        try {
            return CharStreams.toString(new InputStreamReader(executeRaw(client, post).getEntity().getContent(), UTF_8));
        } finally { post.releaseConnection(); }
    }

    public static HttpResponse executeRaw(HttpClient client, HttpRequestBuilder builder) { return executeRaw(client, builder.request()); }

    @SneakyThrows public static <H extends HttpRequestBase> HttpResponse executeRaw(HttpClient client, H post) {
        try {
            return checkStatus(client.execute(post));
        } finally { post.releaseConnection(); }
    }

    private static HttpResponse checkStatus(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (!checkBetween(statusCode, SC_OK, SC_MULTIPLE_CHOICES, true, false)) {
            String errorMessage = CharStreams.toString(new InputStreamReader(response.getEntity().getContent(), UTF_8));
            log.warn(errorMessage); throw new HttpClientException(statusCode, errorMessage);
        }
        return response;
    }

    @SuppressWarnings({"serial", "unused", "WeakerAccess"})
    public static class HttpClientException extends RuntimeException {
        public final int errorCode;

        public HttpClientException(int errorCode, String message) { super(message); this.errorCode = errorCode; }
    }
}