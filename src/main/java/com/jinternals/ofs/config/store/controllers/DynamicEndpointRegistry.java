package com.jinternals.ofs.config.store.controllers;


import com.jinternals.ofs.config.store.configuration.DynamicEndpointProperties;
import com.jinternals.ofs.config.store.configuration.DynamicEndpointProperties.Endpoint;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class DynamicEndpointRegistry {

    private final RequestMappingHandlerMapping handlerMapping;
    private final DynamicController handler;
    private final DynamicEndpointProperties dynamicEndpointProperties;
    private Map<String, RequestMappingInfo> requestMappings = new HashMap<>();
    private Map<HttpMethod, EndpointRegistrar> registrar = new HashMap<>();



    @PostConstruct
    public void registerEndpoints() {

        registrar.put(HttpMethod.POST, this::registerPost);
        registrar.put(HttpMethod.GET, this::registerGet);
        registrar.put(HttpMethod.PUT, this::registerPut);

        dynamicEndpointProperties
                .getEndpoints()
                .forEach(( endpoint) -> registrar.get(endpoint.getMethod()).register(endpoint));


    }

    public void registerPost(Endpoint endpoint) {
        get(endpoint, RequestMethod.POST, ReflectionUtils.findMethod(
                DynamicController.class,
                "handlePost", HttpServletRequest.class, Resource.class, String.class));
    }


    public void registerGet(Endpoint endpoint) {
        if(endpoint.getPath().contains("{id}"))
        {
            get(endpoint, RequestMethod.GET, ReflectionUtils.findMethod(
                    DynamicController.class,
                    "handleGet", HttpServletRequest.class, String.class));
        } else {
            get(endpoint, RequestMethod.GET, ReflectionUtils.findMethod(
                    DynamicController.class,
                    "handleGetByPage", HttpServletRequest.class, Pageable.class));
        }
    }

    private void get(Endpoint endpoint, RequestMethod get, Method handleGet) {
        RequestMappingInfo info = RequestMappingInfo
                .paths(endpoint.getPath())
                .methods(get)
                .build();

        Method method = handleGet;

        handlerMapping.registerMapping(info, handler, method);
        requestMappings.put(endpoint.getPath(), info);
    }

    public void registerPut(Endpoint endpoint) {
        get(endpoint, RequestMethod.PUT, ReflectionUtils.findMethod(
                DynamicController.class,
                "handlePut",
                HttpServletRequest.class,
                Resource.class,
                String.class,
                String.class));
    }


}

