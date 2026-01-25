package com.jinternals.dynamic.rest.exceptions;

import java.util.List;
import java.util.Map;

public class JsonSchemaValidationException extends RuntimeException {
    private final List<Map<String, String>> errors;

    public JsonSchemaValidationException(List<Map<String, String>> errors) {
        super("Json validation failed");
        this.errors = errors;
    }

    public List<Map<String, String>> getErrors() {
        return errors;
    }
}
