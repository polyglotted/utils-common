package io.polyglotted.common.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.polyglotted.common.util.BaseSerializer.serializeBytes;
import static io.polyglotted.common.util.StrUtil.notNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
@RequiredArgsConstructor
public class HttpRequestBuilder {
    @NonNull private final HttpReqType reqType;
    @NonNull private final String baseUri;
    private final String path;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final Multimap<String, String> params = ArrayListMultimap.create();
    private HttpEntity httpEntity;

    public HttpRequestBuilder withBasicAuth(String user, String passwd) {
        return withAuth("Basic " + encodeBase64URLSafeString((user + ":" + passwd).getBytes()));
    }

    public HttpRequestBuilder withBearerAuth(String token) { return withAuth("Bearer " + token); }

    public HttpRequestBuilder withAuth(String authHeader) { if (authHeader != null) { withHeader(AUTHORIZATION, authHeader); } return this; }

    public HttpRequestBuilder withHeader(String key, Object value) { if (value != null) { headers.put(key, value.toString()); } return this; }

    public HttpRequestBuilder withCollParam(String key, Collection<?> values) { for (Object value : values) { withParam(key, value); } return this; }

    public HttpRequestBuilder withParam(String key, Object value) { if (value != null) { params.put(key, value.toString()); } return this; }

    public HttpRequestBuilder withParams(HttpRequestParams params) { return params.update(this); }

    @SneakyThrows public HttpRequestBuilder withContent(Object content) {
        headers.put(CONTENT_TYPE, "application/json"); this.httpEntity = new ByteArrayEntity(serializeBytes(content)); return this;
    }

    @SneakyThrows public HttpRequestBuilder withJson(String json) {
        headers.put(CONTENT_TYPE, "application/json"); this.httpEntity = new StringEntity(json); return this;
    }

    @SneakyThrows public HttpRequestBuilder withInput(String contentType, InputStream stream) {
        headers.put(CONTENT_TYPE, contentType); this.httpEntity = new InputStreamEntity(stream); return this;
    }

    public <H extends HttpRequestBase> H request() {
        H request = reqType.create(buildUri());
        for (Map.Entry<String, String> entry : headers.entrySet()) { request.addHeader(entry.getKey(), entry.getValue()); }
        if (httpEntity != null) { ((HttpEntityEnclosingRequest) request).setEntity(httpEntity); }
        return request;
    }

    @SneakyThrows private URI buildUri() {
        URIBuilder uriBuilder = new URIBuilder(requireNonNull(baseUri, "baseUri is null"));
        if (notNullOrEmpty(path)) { uriBuilder.setPath(path); }
        for (Map.Entry<String, String> entry : params.entries()) { uriBuilder.addParameter(entry.getKey(), entry.getValue()); }
        return uriBuilder.build();
    }

    @SuppressWarnings("unchecked")
    public enum HttpReqType {
        GET {
            @Override HttpGet create(URI uri) { return new HttpGet(uri); }
        },
        POST {
            @Override HttpPost create(URI uri) { return new HttpPost(uri); }
        },
        PUT {
            @Override HttpPut create(URI uri) { return new HttpPut(uri); }
        },
        DELETE {
            @Override HttpDelete create(URI uri) { return new HttpDelete(uri); }
        };

        abstract <H extends HttpRequestBase> H create(URI uri);
    }

    @SuppressWarnings({"WeakerAccess"})
    public static abstract class HttpRequestParams {
        public abstract HttpRequestBuilder update(HttpRequestBuilder builder);
    }
}