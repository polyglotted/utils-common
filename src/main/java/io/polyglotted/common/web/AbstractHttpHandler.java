package io.polyglotted.common.web;

import com.google.common.collect.Sets;
import io.polyglotted.common.util.HttpRequestBuilder.HttpReqType;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static io.polyglotted.common.util.BaseSerializer.MAPPER;
import static io.polyglotted.common.util.ListBuilder.immutableSet;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.Sanitizer.isBinary;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;

@SuppressWarnings({"unused", "WeakerAccess"}) @Slf4j
public abstract class AbstractHttpHandler {
    private final PathRouter<HttpResourceModel> patternRouter = PathRouter.create(25);

    protected AbstractHttpHandler() {
        log.trace("Parsing handler {}", getClass().getName());
        String basePath = "";
        if (getClass().isAnnotationPresent(Path.class)) {
            basePath = getClass().getAnnotation(Path.class).value();
        }

        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getParameterTypes().length >= 2 &&
                method.getParameterTypes()[0].isAssignableFrom(HttpRequest.class) &&
                method.getParameterTypes()[1].isAssignableFrom(HttpResponse.class) &&
                Modifier.isPublic(method.getModifiers())) {

                String relativePath = "";
                if (method.getAnnotation(Path.class) != null) {
                    relativePath = method.getAnnotation(Path.class).value();
                }
                String absolutePath = String.format("%s/%s", basePath, relativePath);
                Set<HttpReqType> httpMethods = getHttpMethods(method);
                checkArgument(httpMethods.size() >= 1, String.format("No HttpMethod found for method: %s", method.getName()));
                log.info("registering " + httpMethods + " on " + absolutePath);

                HttpResourceModel resourceModel = new HttpResourceModel(httpMethods, absolutePath, method);
                log.trace("Adding resource model {}", resourceModel);
                patternRouter.add(absolutePath, resourceModel);
            }
            else {
                log.trace("Not adding method {}({}) to path routing like. HTTP calls will not be routed to this method",
                    method.getName(), method.getParameterTypes());
            }
        }
    }

    protected void handle(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {

        } catch (Exception ex) {
            sendResult(outputStream, SC_INTERNAL_SERVER_ERROR, immutableMap(), ex.getMessage()); return;
        }
    }

    public static <T> void sendResult(OutputStream output, T result) throws IOException { sendResult(output, SC_OK, immutableMap(), result); }

    public static void sendError(OutputStream output, WebException ex) throws IOException { sendResult(output, ex.httpStatus, immutableMap(), ex.getMessage()); }

    public static <T> void sendResult(OutputStream output, int httpStatus, Map<String, String> headers, T result) throws IOException {
        MAPPER.writeValue(output, new GatewayResponse<>(isBinary(result), SC_OK, headers, result));
    }

    private static Set<HttpReqType> getHttpMethods(Method method) {
        Set<HttpReqType> httpMethods = Sets.newHashSet();
        if (method.isAnnotationPresent(GET.class)) { httpMethods.add(HttpReqType.GET); }
        if (method.isAnnotationPresent(PUT.class)) { httpMethods.add(HttpReqType.PUT); }
        if (method.isAnnotationPresent(POST.class)) { httpMethods.add(HttpReqType.POST); }
        if (method.isAnnotationPresent(DELETE.class)) { httpMethods.add(HttpReqType.DELETE); }
        return immutableSet(httpMethods);
    }
}