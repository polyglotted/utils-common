package io.polyglotted.common.web;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_METHOD_NOT_ALLOWED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class WebHttpException extends RuntimeException {
    public final int httpStatus;

    public WebHttpException(int httpStatus, String message) { this(httpStatus, message, null); }

    public WebHttpException(int httpStatus, String message, Throwable cause) {
        super(escapeHtml4(message), cause); this.httpStatus = httpStatus;
    }

    public static WebHttpException asInternalServerException(Throwable ex) {
        return ex.getCause() instanceof WebHttpException ? (WebHttpException) ex.getCause() :
            new WebHttpException(SC_INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    }

    public static WebHttpException internalServerException(String message, Throwable ex) {
        return new WebHttpException(SC_INTERNAL_SERVER_ERROR, message, ex);
    }

    public static WebHttpException internalServerException(String message) { return new WebHttpException(SC_INTERNAL_SERVER_ERROR, message); }

    public static WebHttpException badRequestException(String message) { return new WebHttpException(SC_BAD_REQUEST, message); }

    public static WebHttpException forbiddenException(String message) { return new WebHttpException(SC_FORBIDDEN, message); }

    public static WebHttpException unauthorisedException(String message) { return new WebHttpException(SC_UNAUTHORIZED, message); }

    public static WebHttpException notFoundException(String item) { return new WebHttpException(SC_NOT_FOUND, item + ": Not Found"); }

    public static WebHttpException methodNotAllowedException(String message) { return new WebHttpException(SC_METHOD_NOT_ALLOWED, message); }

    public static WebHttpException conflictException(String message) { return new WebHttpException(SC_CONFLICT, message); }

    public static void checkBadRequest(boolean condition, String message) { checkBadRequest(condition, message, null); }

    public static <T> T checkBadRequest(boolean condition, String message, T r) { if (!condition) { throw badRequestException(message); } return r; }
}