package io.polyglotted.common.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.polyglotted.common.util.BaseSerializer.serialize;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.web.GatewayResponse.sendResult;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

@SuppressWarnings("WeakerAccess")
@Slf4j @RequiredArgsConstructor
public final class HttpResponder {
    final boolean isLoadBalanced;
    final OutputStream outputStream;

    public void sendBytes(int status, byte[] bytes, Map<String, String> headers) {
        sendResult(isLoadBalanced, outputStream, status, Base64.getEncoder().encodeToString(bytes), true, headers);
    }

    public void sendObject(int status, Object result) { sendJson(status, serialize(result)); }

    public void sendJson(int status, String json) { sendString(status, json, "application/json"); }

    public void sendText(int status, String data) { sendString(status, data, "text/plain; charset=utf-8"); }

    public void sendString(int status, String data, String contentType) {
        if (isNullOrEmpty(data)) { sendStatus(status); return; }
        sendResult(isLoadBalanced, outputStream, status, data, false, immutableMap(CONTENT_TYPE, contentType));
    }

    public void sendStatus(int status) { sendStatus(status, immutableMap()); }

    public void sendStatus(int status, Map<String, String> headers) { sendResult(isLoadBalanced, outputStream, status, null, false, headers); }
}