package io.polyglotted.common.test;

import io.polyglotted.common.web.AbstractHttpHandler;
import io.polyglotted.common.web.HttpRequest;
import io.polyglotted.common.web.HttpResponder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static com.google.common.collect.ImmutableMap.of;
import static org.apache.http.HttpStatus.SC_OK;

@SuppressWarnings("UnusedParameters") @Path("/test/v1")
public class HttpDemoHandler extends AbstractHttpHandler {
    @GET @Path("simple")
    public void testGet(HttpRequest request, HttpResponder responder) { responder.sendObject(SC_OK, of("result", "handled in simple")); }
}