package io.polyglotted.common.web;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class WebException extends RuntimeException {
    public final int httpStatus;

    public WebException(int httpStatus, String message) { this(httpStatus, message, null); }

    public WebException(int httpStatus, String message, Throwable cause) {
        super(escapeHtml4(message), cause); this.httpStatus = httpStatus;
    }

    public static WebException asInternalServerException(Throwable ex) {
        return ex.getCause() instanceof WebException ? (WebException) ex.getCause() : new WebException(SC_INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    }

    public static WebException internalServerException(String message, Throwable ex) { return new WebException(SC_INTERNAL_SERVER_ERROR, message, ex); }

    public static WebException internalServerException(String message) { return new WebException(SC_INTERNAL_SERVER_ERROR, message); }

    public static WebException badRequestException(String message) { return new WebException(SC_BAD_REQUEST, message); }

    public static WebException forbiddenException(String message) { return new WebException(SC_FORBIDDEN, message); }

    public static WebException unauthorisedException(String message) { return new WebException(SC_UNAUTHORIZED, message); }

    public static WebException notFoundException(String item) { return new WebException(SC_NOT_FOUND, item + " not found"); }

    public static WebException conflictException(String message) { return new WebException(SC_CONFLICT, message); }

    public static void checkBadRequest(boolean condition, String message) { checkBadRequest(condition, message, null); }

    public static <T> T checkBadRequest(boolean condition, String message, T r) { if (!condition) { throw badRequestException(message); } return r; }
}