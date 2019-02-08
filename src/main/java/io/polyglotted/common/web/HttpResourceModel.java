package io.polyglotted.common.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.polyglotted.common.util.HttpRequestBuilder.HttpReqType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.web.ParamConvertUtils.createHeaderParamConverter;
import static io.polyglotted.common.web.ParamConvertUtils.createPathParamConverter;
import static io.polyglotted.common.web.ParamConvertUtils.createQueryParamConverter;

@SuppressWarnings("WeakerAccess") @Accessors
@ToString(of = {"httpMethods", "path", "method"})
public final class HttpResourceModel {
    private static final Set<Class<? extends Annotation>> SUPPORTED_PARAM_ANNOTATIONS =
        ImmutableSet.of(PathParam.class, QueryParam.class, HeaderParam.class);

    @Getter private final Set<HttpReqType> httpMethods;
    @Getter private final String path;
    @Getter private final Method method;
    private final List<Map<Class<? extends Annotation>, ParameterInfo<?>>> paramsInfo;

    HttpResourceModel(Set<HttpReqType> httpMethods, String path, Method method) {
        this.httpMethods = httpMethods;
        this.path = path;
        this.method = method;
        this.paramsInfo = createParametersInfos(method);
    }

    private List<Map<Class<? extends Annotation>, ParameterInfo<?>>> createParametersInfos(Method method) {
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
                    parameterInfo = ParameterInfo.create(annotation, createPathParamConverter(parameterTypes[i]));
                }
                else if (QueryParam.class.isAssignableFrom(annotationType)) {
                    parameterInfo = ParameterInfo.create(annotation, createQueryParamConverter(parameterTypes[i]));
                }
                else if (HeaderParam.class.isAssignableFrom(annotationType)) {
                    parameterInfo = ParameterInfo.create(annotation, createHeaderParamConverter(parameterTypes[i]));
                }
                else {
                    parameterInfo = ParameterInfo.create(annotation, null);
                }

                paramAnnotations.put(annotationType, parameterInfo);
            }

            // Must have either @PathParam, @QueryParam or @HeaderParam, but not two or more.
            if (Sets.intersection(SUPPORTED_PARAM_ANNOTATIONS, paramAnnotations.keySet()).size() != 1) {
                throw new IllegalArgumentException(
                    String.format("Must have exactly one annotation from %s for parameter %d in method %s", SUPPORTED_PARAM_ANNOTATIONS, i, method));
            }
            result.add(immutableMap(paramAnnotations));
        }

        return result.build();
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