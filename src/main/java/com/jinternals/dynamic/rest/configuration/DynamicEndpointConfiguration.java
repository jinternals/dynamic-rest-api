package com.jinternals.dynamic.rest.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;



@Configuration
@EnableConfigurationProperties(DynamicEndpointProperties.class)
public class DynamicEndpointConfiguration {

}


