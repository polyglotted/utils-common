package io.polyglotted.common.test;

import io.polyglotted.common.model.Pair;
import io.polyglotted.common.web.AbstractGatewayHandler;
import io.polyglotted.common.web.WebHttpRequest;
import io.polyglotted.common.web.WebHttpResponder;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

import static io.polyglotted.common.util.EncodingUtil.uriSuffixPair;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static org.apache.http.HttpStatus.SC_OK;

@SuppressWarnings("UnusedParameters") @Path("/test/v1")
public class GatewayDemoHandler extends AbstractGatewayHandler {
    @GET @Path("simple")
    public void testGet(WebHttpRequest request, WebHttpResponder responder) { responder.sendObject(immutableMap("result", "ok")); }

    @GET @Path("tweets/{id}")
    public void testGetTweet(WebHttpRequest request, WebHttpResponder responder, @PathParam("id") String id) {
        responder.sendObject(immutableMap("result", "ok"));
    }

    @PUT @Path("tweets/{id}")
    public void testPutTweet(WebHttpRequest request, WebHttpResponder responder, @PathParam("id") String id) {
        responder.sendBytes(SC_OK, "result".getBytes());
    }

    @DELETE @Path("facebook/{id}/message")
    public void testNoMethodRoute(WebHttpRequest request, WebHttpResponder responder, @PathParam("id") String id) { System.out.println(); }

    @GET @Path("/exception")
    public void exception(WebHttpRequest request, WebHttpResponder responder) { throw new IllegalStateException("Illegal argument"); }

    @GET @Path("/multi-match/foo")
    public void multiMatchExactFoo(WebHttpRequest request, WebHttpResponder responder) { responder.sendJson("{\"result\":\"foo\"}"); }

    @GET @Path("/multi-match/**")
    public void multiMatchAll(WebHttpRequest request, WebHttpResponder responder) { responder.sendJson("{\"result\":\"ok\"}"); }

    @GET @Path("/multi-match-pqp/**")
    public void multiMatchPairWithQp(WebHttpRequest request, WebHttpResponder responder, @QueryParam("p1") String p1,
                                     @DefaultValue("p2") @QueryParam("p2") String p2) {
        Pair<String, String> pair = uriSuffixPair(request.uriPath, "/multi-match-pqp/");
        responder.sendString("multi-match-pqp-" + pair._a + "-" + pair._b + ":" + p1 + ":" + p2, "text/plain; charset=utf-8");
    }

    @GET @Path("/multi-match-multi/**")
    public void multiMatchMulti(WebHttpRequest request, WebHttpResponder responder, @QueryParam("pList") List<String> pList) {
        Pair<String, String> pair = uriSuffixPair(request.uriPath, "/multi-match-multi/");
        responder.sendString("multi-match-multi-" + pair._a + "-" + pair._b + ":" + String.join(",", pList), "text/plain; charset=utf-8");
    }
}