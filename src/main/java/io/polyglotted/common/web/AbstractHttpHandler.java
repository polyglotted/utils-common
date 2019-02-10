package io.polyglotted.common.web;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import io.polyglotted.common.model.MapResult.SimpleMapResult;
import io.polyglotted.common.web.PathRouter.RoutableDestination;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static io.polyglotted.common.util.Assertions.checkBool;
import static io.polyglotted.common.util.BaseSerializer.deserialize;
import static io.polyglotted.common.util.ListBuilder.immutableSet;
import static io.polyglotted.common.util.NullUtil.nonNull;
import static io.polyglotted.common.web.GatewayResponse.sendError;
import static io.polyglotted.common.web.PathRouter.GROUP_PATTERN;
import static io.polyglotted.common.web.WebException.methodNotAllowedException;
import static io.polyglotted.common.web.WebException.notFoundException;

@SuppressWarnings({"unused", "WeakerAccess"}) @Slf4j
public abstract class AbstractHttpHandler {
    private static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings();
    private final PathRouter<HttpResourceModel> patternRouter = PathRouter.create(25);

    protected AbstractHttpHandler() {
        String basePath = "";
        if (getClass().isAnnotationPresent(Path.class)) {
            basePath = getClass().getAnnotation(Path.class).value();
        }

        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getParameterTypes().length >= 2 &&
                method.getParameterTypes()[0].isAssignableFrom(HttpRequest.class) &&
                method.getParameterTypes()[1].isAssignableFrom(HttpResponder.class) &&
                Modifier.isPublic(method.getModifiers())) {

                String relativePath = "";
                if (method.getAnnotation(Path.class) != null) {
                    relativePath = method.getAnnotation(Path.class).value();
                }
                String absolutePath = basePath + "/" + relativePath;
                Set<HttpMethod> httpMethods = getHttpMethods(method);
                checkBool(httpMethods.size() >= 1, "No HttpMethod found for method: " + method.getName());
                log.info("registering " + httpMethods + " on " + absolutePath);

                HttpResourceModel resourceModel = new HttpResourceModel(httpMethods, absolutePath, method);
                log.trace("Adding resource model {}", resourceModel);
                patternRouter.add(absolutePath, resourceModel);
            }
        }
    }

    public final void handle(InputStream inputStream, OutputStream outputStream) {
        SimpleMapResult event = deserialize(inputStream, SimpleMapResult.class);
        boolean isLoadBalanced = nonNull(event.deepRetrieve("requestContext.elb"), false);

        HttpRequest request;
        try {
            request = HttpRequest.from(event);
        } catch (Exception ex) { sendError(isLoadBalanced, outputStream, ex); return; }
        try {
            List<RoutableDestination<HttpResourceModel>> routableDestinations = patternRouter.getDestinations(request.uriPath);
            if (routableDestinations.isEmpty()) { throw notFoundException(request.uriPath); }

            RoutableDestination<HttpResourceModel> matchedDestination = getMatchedDestination(routableDestinations, request.method, request.uriPath);
            if (matchedDestination == null) {
                throw methodNotAllowedException(request.uriPath + ": Method Not Allowed");
            }
            HttpResourceModel httpResourceModel = matchedDestination.destination;
            httpResourceModel.handle(this, request, new HttpResponder(isLoadBalanced, outputStream), matchedDestination.groupNameValues);

        } catch (Exception ex) { sendError(isLoadBalanced, outputStream, ex); }
    }

    private RoutableDestination<HttpResourceModel> getMatchedDestination(List<RoutableDestination<HttpResourceModel>> routableDestinations,
                                                                         HttpMethod targetHttpMethod, String requestUri) {
        Iterable<String> requestUriParts = SPLITTER.split(requestUri);
        List<RoutableDestination<HttpResourceModel>> matchedDestinations = newArrayListWithExpectedSize(routableDestinations.size());

        long maxScore = 0;
        for (RoutableDestination<HttpResourceModel> destination : routableDestinations) {
            HttpResourceModel resourceModel = destination.destination;
            for (HttpMethod httpMethod : resourceModel.httpMethods()) {
                if (targetHttpMethod.equals(httpMethod)) {
                    long score = getWeightedMatchScore(requestUriParts, SPLITTER.split(resourceModel.path()));
                    log.trace("Max score = {}. Weighted score for {} is {}. ", maxScore, destination, score);

                    if (score > maxScore) {
                        maxScore = score;
                        matchedDestinations.clear();
                        matchedDestinations.add(destination);
                    }
                    else if (score == maxScore) {
                        matchedDestinations.add(destination);
                    }
                }
            }
        }
        if (matchedDestinations.size() > 1) {
            throw new IllegalStateException("Multiple matched handlers found for request uri " + requestUri + ": " + matchedDestinations);
        }
        return matchedDestinations.size() == 1 ? matchedDestinations.get(0) : null;
    }

    private static long getWeightedMatchScore(Iterable<String> requestUriParts, Iterable<String> destUriParts) {
        long score = 0;
        for (Iterator<String> rit = requestUriParts.iterator(), dit = destUriParts.iterator(); rit.hasNext() && dit.hasNext(); ) {
            String requestPart = rit.next(), destPart = dit.next();

            if (requestPart.equals(destPart)) { score = (score * 5) + 4; }
            else if (GROUP_PATTERN.matcher(destPart).matches()) { score = (score * 5) + 3; }
            else { score = (score * 5) + 2; }
        }
        return score;
    }

    private static Set<HttpMethod> getHttpMethods(Method method) {
        Set<HttpMethod> httpMethods = Sets.newHashSet();
        if (method.isAnnotationPresent(GET.class)) { httpMethods.add(HttpMethod.GET); }
        if (method.isAnnotationPresent(PUT.class)) { httpMethods.add(HttpMethod.PUT); }
        if (method.isAnnotationPresent(POST.class)) { httpMethods.add(HttpMethod.POST); }
        if (method.isAnnotationPresent(DELETE.class)) { httpMethods.add(HttpMethod.DELETE); }
        return immutableSet(httpMethods);
    }
}