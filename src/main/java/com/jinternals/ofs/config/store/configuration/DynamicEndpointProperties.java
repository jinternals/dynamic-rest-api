package com.jinternals.ofs.config.store.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties("ofs.dynamic")
public class DynamicEndpointProperties {

    @NestedConfigurationProperty
    private List<Endpoint> endpoints =  new ArrayList<>();

    @Data
    public static class Endpoint {
        private String path;
        private Resource schema;
        private HttpMethod method = HttpMethod.GET;
    }
}
