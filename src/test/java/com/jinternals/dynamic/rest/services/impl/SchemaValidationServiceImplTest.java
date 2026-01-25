package com.jinternals.dynamic.rest.services.impl;

import com.jinternals.dynamic.rest.services.SchemaCacheService;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaValidationServiceImplTest {

    @Mock
    private SchemaCacheService schemaCacheService;

    @Mock
    private Resource schemaResource;

    @InjectMocks
    private SchemaValidationServiceImpl schemaValidationService;

    private final SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_7);

    @Test
    void shouldReturnEmptyListWhenSchemaResourceIsNull() {
        List<Map<String, String>> result = schemaValidationService.validate("{}", null);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenSchemaResourceDoesNotExist() {
        when(schemaResource.exists()).thenReturn(false);
        List<Map<String, String>> result = schemaValidationService.validate("{}", schemaResource);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenValidationPasses() {
        String schemaJson = """
                {
                  "$schema": "http://json-schema.org/draft-07/schema#",
                  "type": "object",
                  "properties": {
                    "name": { "type": "string" }
                  }
                }
                """;
        String body = "{\"name\": \"John\"}";
        Schema schema = registry.getSchema(new ByteArrayInputStream(schemaJson.getBytes()));

        when(schemaResource.exists()).thenReturn(true);
        when(schemaCacheService.getSchema(schemaResource)).thenReturn(schema);

        List<Map<String, String>> result = schemaValidationService.validate(body, schemaResource);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnErrorsWhenValidationFails() {
        String schemaJson = """
                {
                  "$schema": "http://json-schema.org/draft-07/schema#",
                  "type": "object",
                  "properties": {
                    "age": { "type": "integer" }
                  }
                }
                """;
        String body = "{\"age\": \"not-an-integer\"}"; // Invalid type
        Schema schema = registry.getSchema(new ByteArrayInputStream(schemaJson.getBytes()));

        when(schemaResource.exists()).thenReturn(true);
        when(schemaCacheService.getSchema(schemaResource)).thenReturn(schema);

        List<Map<String, String>> result = schemaValidationService.validate(body, schemaResource);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).containsEntry("field", "age");
        assertThat(result.get(0)).hasEntrySatisfying("message",  field -> field.contains("integer"));

    }

    @Test
    void shouldReturnErrorsWhenRequiredFieldMissing() {
        String schemaJson = """
                {
                  "$schema": "http://json-schema.org/draft-07/schema#",
                  "type": "object",
                  "required": ["name"]
                }
                """;
        String body = "{}";
        Schema schema = registry.getSchema(new ByteArrayInputStream(schemaJson.getBytes()));

        when(schemaResource.exists()).thenReturn(true);
        when(schemaCacheService.getSchema(schemaResource)).thenReturn(schema);

        List<Map<String, String>> result = schemaValidationService.validate(body, schemaResource);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).containsEntry("field", "name");
        assertThat(result.get(0)).hasEntrySatisfying("message",  field -> field.contains("required"));

    }

    @Test
    void shouldReturnErrorsWhenNestedRequiredFieldMissing() {
        String schemaJson = """
                   {
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "type": "object",
                    "properties": {
                      "user": {
                        "type": "object",
                        "required": ["id"]
                      }
                    }
                  }
                """;
        String body = "{\"user\": {}}";
        Schema schema = registry.getSchema(new ByteArrayInputStream(schemaJson.getBytes()));

        when(schemaResource.exists()).thenReturn(true);
        when(schemaCacheService.getSchema(schemaResource)).thenReturn(schema);

        List<Map<String, String>> result = schemaValidationService.validate(body, schemaResource);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).containsEntry("field", "user.id");
        assertThat(result.get(0)).hasEntrySatisfying("message",  field -> field.contains("required"));

    }

    @Test
    void shouldHandleRequiredErrorWithMissingArguments() {
        Schema mockedSchema = org.mockito.Mockito.mock(Schema.class);
        com.networknt.schema.Error mockError = org.mockito.Mockito.mock(com.networknt.schema.Error.class,
                org.mockito.Answers.RETURNS_DEEP_STUBS);

        when(schemaResource.exists()).thenReturn(true);
        when(schemaCacheService.getSchema(schemaResource)).thenReturn(mockedSchema);
        when(mockedSchema.validate(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(mockError));

        when(mockError.getKeyword()).thenReturn("required");
        when(mockError.getArguments()).thenReturn(new Object[]{});
        when(mockError.getInstanceLocation().toString()).thenReturn("/some/path");
        when(mockError.getMessage()).thenReturn("Required field missing");

        List<Map<String, String>> result = schemaValidationService.validate("{}", schemaResource);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).containsEntry("field", "some.path");
        assertThat(result.get(0)).containsEntry("message", "Required field missing");
    }
}
