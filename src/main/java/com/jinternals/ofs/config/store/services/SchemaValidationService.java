package com.jinternals.ofs.config.store.services;

import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

public interface SchemaValidationService {
    @SneakyThrows
    List<Map<String, String>> validate(String body, Resource schemaResource);
}
