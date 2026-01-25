package com.jinternals.dynamic.rest.controllers;

import com.jinternals.dynamic.rest.exceptions.JsonSchemaValidationException;
import com.jinternals.dynamic.rest.services.DynamicDataService;
import com.jinternals.dynamic.rest.services.SchemaValidationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicControllerTest {

    @Mock
    private DynamicDataService dynamicDataService;

    @Mock
    private SchemaValidationService schemaValidationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Resource schemaResource;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private DynamicController dynamicController;

    @Test
    void shouldSaveDataWhenValidationPassesForPost() {
        String body = "{\"key\":\"value\"}";
        String savedId = UUID.randomUUID().toString();

        when(schemaValidationService.validate(body, schemaResource)).thenReturn(Collections.emptyList());
        when(dynamicDataService.saveData(anyString(), eq(body))).thenReturn(savedId);

        ResponseEntity<?> response = dynamicController.handlePost(request, schemaResource, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(savedId);
    }

    @Test
    void shouldThrowExceptionWhenValidationFailsForPost() {
        String body = "{\"key\":\"value\"}";
        List<Map<String, String>> errors = List.of(Map.of("field", "key", "message", "error"));

        when(schemaValidationService.validate(body, schemaResource)).thenReturn(errors);

        assertThatThrownBy(() -> dynamicController.handlePost(request, schemaResource, body))
                .isInstanceOf(JsonSchemaValidationException.class);
    }

    @Test
    void shouldSaveDataWhenValidationPassesForPut() {
        String id = "test-id";
        String body = "{\"key\":\"value\"}";

        when(schemaValidationService.validate(body, schemaResource)).thenReturn(Collections.emptyList());
        when(dynamicDataService.saveData(id, body)).thenReturn(id);

        ResponseEntity<?> response = dynamicController.handlePut(request, schemaResource, id, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(id);
    }

    @Test
    void shouldThrowExceptionWhenValidationFailsForPut() {
        String id = "test-id";
        String body = "{\"key\":\"value\"}";
        List<Map<String, String>> errors = List.of(Map.of("field", "key", "message", "error"));

        when(schemaValidationService.validate(body, schemaResource)).thenReturn(errors);

        assertThatThrownBy(() -> dynamicController.handlePut(request, schemaResource, id, body))
                .isInstanceOf(JsonSchemaValidationException.class);
    }

    @Test
    void shouldReturnDataForGet() {
        String id = "test-id";
        String data = "{\"key\":\"value\"}";

        when(dynamicDataService.getData(id)).thenReturn(data);

        ResponseEntity<String> response = dynamicController.handleGet(request, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(data);
    }

    @Test
    void shouldReturnDataForGetByPage() {
        String data = "[{\"key\":\"value\"}]";

        when(dynamicDataService.getData(pageable)).thenReturn(data);

        ResponseEntity<String> response = dynamicController.handleGetByPage(request, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(data);
    }
}
