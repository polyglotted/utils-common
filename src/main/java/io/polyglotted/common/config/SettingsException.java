package io.polyglotted.common.config;

@SuppressWarnings("serial")
public class SettingsException extends RuntimeException {
    public SettingsException(String message) { super(message); }

    public SettingsException(Throwable cause) { super(cause); }
}