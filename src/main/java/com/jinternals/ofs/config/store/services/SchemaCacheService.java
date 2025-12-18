package com.jinternals.ofs.config.store.services;

import com.networknt.schema.Schema;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

public interface SchemaCacheService {
    @SneakyThrows
    Schema getSchema(Resource resource);
}
