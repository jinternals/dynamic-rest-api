package com.jinternals.dynamic.rest.services.impl;

import com.networknt.schema.Schema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaCacheServiceImplTest {

    @Mock
    private Resource resource;

    @InjectMocks
    private SchemaCacheServiceImpl schemaCacheService;

    @Test
    void shouldLoadSchemaWhenNotCached() throws IOException {
        String schemaContent = """
                {
                  "$schema": "http://json-schema.org/draft-07/schema#",
                  "type": "object",
                  "properties": {
                    "name": { "type": "string" }
                  }
                }
                """;

        when(resource.getFilename()).thenReturn("test-schema.json");
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(schemaContent.getBytes()));

        Schema schema = schemaCacheService.getSchema(resource);

        assertThat(schema).isNotNull();
        verify(resource).getInputStream();
    }

    @Test
    void shouldReturnCachedSchemaWhenCalledTwice() throws IOException {
        String schemaContent = """
                {
                  "$schema": "http://json-schema.org/draft-07/schema#",
                  "type": "object",
                  "properties": {
                    "name": { "type": "string" }
                  }
                }
                """;

        when(resource.getFilename()).thenReturn("test-schema.json");
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(schemaContent.getBytes()));

        Schema schema1 = schemaCacheService.getSchema(resource);
        Schema schema2 = schemaCacheService.getSchema(resource);

        assertThat(schema1).isNotNull();
        assertThat(schema2).isNotNull();
        assertThat(schema1).isSameAs(schema2);

        // Verification call count: getInputStream() should be called only once
        verify(resource, times(1)).getInputStream();
    }
}
