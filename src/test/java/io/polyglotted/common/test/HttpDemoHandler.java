package io.polyglotted.common.test;

import io.polyglotted.common.model.Pair;
import io.polyglotted.common.web.AbstractHttpHandler;
import io.polyglotted.common.web.HttpRequest;
import io.polyglotted.common.web.HttpResponder;

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
public class HttpDemoHandler extends AbstractHttpHandler {
    @GET @Path("simple")
    public void testGet(HttpRequest request, HttpResponder responder) { responder.sendObject(immutableMap("result", "ok")); }

    @GET @Path("tweets/{id}")
    public void testGetTweet(HttpRequest request, HttpResponder responder, @PathParam("id") String id) {
        responder.sendObject(immutableMap("result", "ok"));
    }

    @PUT @Path("tweets/{id}")
    public void testPutTweet(HttpRequest request, HttpResponder responder, @PathParam("id") String id) {
        responder.sendBytes(SC_OK, "result".getBytes());
    }

    @DELETE @Path("facebook/{id}/message")
    public void testNoMethodRoute(HttpRequest request, HttpResponder responder, @PathParam("id") String id) { System.out.println(); }

    @GET @Path("/exception")
    public void exception(HttpRequest request, HttpResponder responder) { throw new IllegalStateException("Illegal argument"); }

    @GET @Path("/multi-match/foo")
    public void multiMatchExactFoo(HttpRequest request, HttpResponder responder) { responder.sendJson("{\"result\":\"foo\"}"); }

    @GET @Path("/multi-match/**")
    public void multiMatchAll(HttpRequest request, HttpResponder responder) { responder.sendJson("{\"result\":\"ok\"}"); }

    @GET @Path("/multi-match-pqp/**")
    public void multiMatchPairWithQp(HttpRequest request, HttpResponder responder, @QueryParam("p1") String p1,
                                     @DefaultValue("p2") @QueryParam("p2") String p2) {
        Pair<String, String> pair = uriSuffixPair(request.uriPath, "/multi-match-pqp/");
        responder.sendString("multi-match-pqp-" + pair._a + "-" + pair._b + ":" + p1 + ":" + p2, "text/plain; charset=utf-8");
    }

    @GET @Path("/multi-match-multi/**")
    public void multiMatchMulti(HttpRequest request, HttpResponder responder, @QueryParam("pList") List<String> pList) {
        Pair<String, String> pair = uriSuffixPair(request.uriPath, "/multi-match-multi/");
        responder.sendString("multi-match-multi-" + pair._a + "-" + pair._b + ":" + String.join(",", pList), "text/plain; charset=utf-8");
    }
}