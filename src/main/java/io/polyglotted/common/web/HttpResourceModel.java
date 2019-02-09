package io.polyglotted.common.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.ListBuilder.immutableSet;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.web.ParamConvertUtils.createPathParamConverter;
import static io.polyglotted.common.web.ParamConvertUtils.createQueryParamConverter;
import static io.polyglotted.common.web.WebException.internalServerException;
import static io.polyglotted.common.web.WebException.methodNotAllowedException;
import static java.util.Objects.requireNonNull;

@SuppressWarnings({"WeakerAccess", "unchecked"})
@Accessors(fluent = true) @ToString(of = {"httpMethods", "path", "method"})
public final class HttpResourceModel {
    private static final Set<Class<? extends Annotation>> SUPPORTED_PARAM_ANNOTATIONS = immutableSet(PathParam.class, QueryParam.class);

    @Getter private final Set<HttpMethod> httpMethods;
    @Getter private final String path;
    private final Method method;
    private final List<Map<Class<? extends Annotation>, ParameterInfo<?>>> paramsInfo;

    HttpResourceModel(Set<HttpMethod> httpMethods, String path, Method method) {
        this.httpMethods = httpMethods;
        this.path = path;
        this.method = method;
        this.paramsInfo = createParametersInfos(method);
    }

    private static List<Map<Class<? extends Annotation>, ParameterInfo<?>>> createParametersInfos(Method method) {
        if (method.getParameterTypes().length <= 2) { return ImmutableList.of(); }
        ImmutableList.Builder<Map<Class<? extends Annotation>, ParameterInfo<?>>> result = ImmutableList.builder();
        Type[] parameterTypes = method.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 2; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            Map<Class<? extends Annotation>, ParameterInfo<?>> paramAnnotations = Maps.newIdentityHashMap();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                ParameterInfo<?> parameterInfo;
                if (PathParam.class.isAssignableFrom(annotationType)) {
                    parameterInfo = new ParameterInfo<>(annotation, createPathParamConverter(parameterTypes[i]));
                }
                else if (QueryParam.class.isAssignableFrom(annotationType)) {
                    parameterInfo = new ParameterInfo<>(annotation, createQueryParamConverter(parameterTypes[i]));
                }
                else {
                    parameterInfo = new ParameterInfo<>(annotation, null);
                }
                paramAnnotations.put(annotationType, parameterInfo);
            }

            // Must have either @PathParam, @QueryParam or @HeaderParam, but not two or more.
            if (Sets.intersection(SUPPORTED_PARAM_ANNOTATIONS, paramAnnotations.keySet()).size() != 1) {
                throw new IllegalArgumentException(String.format("Must have exactly one annotation from %s " +
                    "for parameter %d in method %s", SUPPORTED_PARAM_ANNOTATIONS, i, method));
            }
            result.add(immutableMap(paramAnnotations));
        }
        return result.build();
    }

    void handle(AbstractHttpHandler handler, HttpRequest request, HttpResponder responder, Map<String, String> groupValues) {
        try {
            if (httpMethods.contains(request.method)) {
                //Setup args for reflection call
                Object[] args = new Object[paramsInfo.size() + 2];
                args[0] = request; args[1] = responder;

                int idx = 2;
                for (Map<Class<? extends Annotation>, ParameterInfo<?>> info : paramsInfo) {
                    if (info.containsKey(PathParam.class)) {
                        args[idx++] = getPathParamValue(info, groupValues);
                    }
                    else if (info.containsKey(QueryParam.class)) {
                        args[idx++] = getQueryParamValue(info, request);
                    }
                }
                method.invoke(handler, args);
            }
            else {
                throw methodNotAllowedException(String.format("Problem accessing:\"%s\" Reason: Method Not Allowed", request.uriPath));
            }
        } catch (Throwable e) {
            throw internalServerException(String.format("Error in executing request: %s %s", request.method, request.uriPath), e);
        }
    }

    private static Object getPathParamValue(Map<Class<? extends Annotation>, ParameterInfo<?>> annotations, Map<String, String> groupValues) {
        ParameterInfo<String> info = (ParameterInfo<String>) annotations.get(PathParam.class);
        PathParam pathParam = info.getAnnotation();
        String value = groupValues.get(pathParam.value());
        return info.convert(requireNonNull(value, "Could not resolve value for parameter " + pathParam.value()));
    }

    private static Object getQueryParamValue(Map<Class<? extends Annotation>, ParameterInfo<?>> annotations, HttpRequest request) {
        ParameterInfo<Object> info = (ParameterInfo<Object>) annotations.get(QueryParam.class);
        QueryParam queryParam = info.getAnnotation();
        Object values = request.queryParams.get(queryParam.value());
        return (values == null) ? info.convert(defaultValue(annotations)) : info.convert(values);
    }

    private static List<String> defaultValue(Map<Class<? extends Annotation>, ParameterInfo<?>> annotations) {
        ParameterInfo<?> defaultInfo = annotations.get(DefaultValue.class);
        return (defaultInfo != null) ? immutableList(defaultInfo.<DefaultValue>getAnnotation().value()) : immutableList();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ParameterInfo<T> {
        private final Annotation annotation;
        private final Function<T, Object> converter;

        static <V> ParameterInfo<V> create(Annotation annotation, Function<V, Object> function) { return new ParameterInfo<>(annotation, function); }

        @SuppressWarnings("unchecked") <V extends Annotation> V getAnnotation() { return (V) annotation; }

        Object convert(T input) { return (converter == null) ? null : converter.apply(input); }
    }
}