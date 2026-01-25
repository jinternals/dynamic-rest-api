package com.jinternals.dynamic.rest.configuration;

import com.jinternals.dynamic.rest.handlers.SchemaArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration  implements WebMvcConfigurer {
    private final DynamicEndpointProperties dynamicEndpointProperties;
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SchemaArgumentResolver(dynamicEndpointProperties));
    }
}
