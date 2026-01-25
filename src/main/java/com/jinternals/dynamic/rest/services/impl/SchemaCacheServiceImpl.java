package com.jinternals.dynamic.rest.services.impl;

import com.jinternals.dynamic.rest.services.SchemaCacheService;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SchemaCacheServiceImpl implements SchemaCacheService {

    private final SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_7);
    private final Map<String, Schema> schemaCache = new ConcurrentHashMap<>();

    @SneakyThrows
    @Override
    public Schema getSchema(Resource resource) {
        String cacheKey = resource.getFilename();
        return schemaCache.computeIfAbsent(cacheKey, key -> loadSchema(resource));
    }

    @SneakyThrows
    private Schema loadSchema(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            return registry.getSchema(is);
        }
    }
}
