package com.jinternals.ofs.config.store.controllers;

import com.jinternals.ofs.config.store.services.DynamicDataService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import com.jinternals.ofs.config.store.exceptions.JsonSchemaValidationException;
import com.jinternals.ofs.config.store.services.SchemaValidationService;

import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@Component
@RequiredArgsConstructor
public class DynamicController {

    private final DynamicDataService dynamicDataService;
    private final SchemaValidationService schemaValidationService;

    @SneakyThrows
    public ResponseEntity<?> handlePost(
            HttpServletRequest request,
            Resource schemaResource,
            @RequestBody(required = false) String body) {
        List<Map<String, String>> errors = schemaValidationService.validate(body, schemaResource);
        if (!errors.isEmpty()) {
            throw new JsonSchemaValidationException(errors);
        }
        return ok(dynamicDataService.saveData(UUID.randomUUID().toString(), body));
    }

    @SneakyThrows
    public ResponseEntity<?> handlePut(
            HttpServletRequest request,
            Resource schemaResource,
            @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        List<Map<String, String>> errors = schemaValidationService.validate(body, schemaResource);
        if (!errors.isEmpty()) {
            throw new JsonSchemaValidationException(errors);
        }
        return ok(dynamicDataService.saveData(id, body));
    }

    public ResponseEntity<String> handleGet(
            HttpServletRequest request,
            @PathVariable("id") String id) {
        return ok(dynamicDataService.getData(id));
    }

    public ResponseEntity<String> handleGetByPage(
            HttpServletRequest request,
            @PageableDefault Pageable pageable) {
        return ok(dynamicDataService.getData(pageable));
    }

}
