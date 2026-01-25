package com.jinternals.dynamic.rest.services.impl;

import com.jinternals.dynamic.rest.services.SchemaValidationService;
import com.jinternals.dynamic.rest.services.SchemaCacheService;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.networknt.schema.Error;

@Service
@RequiredArgsConstructor
public class SchemaValidationServiceImpl implements SchemaValidationService {

    private final SchemaCacheService schemaCacheService;

    @SneakyThrows
    @Override
    public List<Map<String, String>> validate(String body, Resource schemaResource) {
        if (schemaResource == null || !schemaResource.exists()) {
            return Collections.emptyList();
        }

        Schema schema = schemaCacheService.getSchema(schemaResource);

        List<Error> validationMessages = schema.validate(body, InputFormat.JSON);

        if (validationMessages.isEmpty()) {
            return Collections.emptyList();
        }

        return validationMessages.stream()
                .map(this::mapError)
                .toList();
    }

    private Map<String, String> mapError(Error message) {
        Map<String, String> error = new HashMap<>();
        error.put("field", resolveField(message));
        error.put("message", message.getMessage());
        return error;
    }

    private String resolveField(Error message) {
        String field = message.getInstanceLocation().toString();

        if ("required".equals(message.getKeyword())) {
            Object[] arguments = message.getArguments();
            if (arguments != null && arguments.length > 0) {
                String missingField = arguments[0].toString();
                field = field.isEmpty() ? "/" + missingField : field + "/" + missingField;
            }
        }

        return normalize(field);
    }

    private static @NonNull String normalize(String field) {
        var value = field.startsWith("/") ? field.substring(1) : field;
        return value.replace("/", ".");
    }

}
