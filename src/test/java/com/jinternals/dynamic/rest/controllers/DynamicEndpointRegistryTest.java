package com.jinternals.dynamic.rest.controllers;

import com.jinternals.dynamic.rest.configuration.DynamicEndpointProperties;
import com.jinternals.dynamic.rest.configuration.DynamicEndpointProperties.Endpoint;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicEndpointRegistryTest {

    @Mock
    private RequestMappingHandlerMapping handlerMapping;

    @Mock
    private DynamicController handler;

    @Mock
    private DynamicEndpointProperties dynamicEndpointProperties;

    @InjectMocks
    private DynamicEndpointRegistry dynamicEndpointRegistry;

    @Captor
    private ArgumentCaptor<RequestMappingInfo> requestMappingInfoCaptor;

    @Captor
    private ArgumentCaptor<Method> methodCaptor;

    @Test
    void shouldRegisterPostEndpoint() throws NoSuchMethodException {
        Endpoint endpoint = new Endpoint();
        endpoint.setPath("/api/v1/resource");
        endpoint.setMethod(HttpMethod.POST);

        when(dynamicEndpointProperties.getEndpoints()).thenReturn(Collections.singletonList(endpoint));

        dynamicEndpointRegistry.registerEndpoints();

        verify(handlerMapping).registerMapping(requestMappingInfoCaptor.capture(), eq(handler), methodCaptor.capture());

        RequestMappingInfo info = requestMappingInfoCaptor.getValue();
        assertThat(info.getPathPatternsCondition().getPatternValues()).contains("/api/v1/resource");
        assertThat(info.getMethodsCondition().getMethods()).contains(RequestMethod.POST);

        Method method = methodCaptor.getValue();
        Method expectedMethod = DynamicController.class.getDeclaredMethod("handlePost", HttpServletRequest.class,
                Resource.class, String.class);
        assertThat(method).isEqualTo(expectedMethod);
    }

    @Test
    void shouldRegisterGetEndpointWithId() throws NoSuchMethodException {
        Endpoint endpoint = new Endpoint();
        endpoint.setPath("/api/v1/resource/{id}");
        endpoint.setMethod(HttpMethod.GET);

        when(dynamicEndpointProperties.getEndpoints()).thenReturn(Collections.singletonList(endpoint));

        dynamicEndpointRegistry.registerEndpoints();

        verify(handlerMapping).registerMapping(requestMappingInfoCaptor.capture(), eq(handler), methodCaptor.capture());

        RequestMappingInfo info = requestMappingInfoCaptor.getValue();
        assertThat(info.getPathPatternsCondition().getPatternValues()).contains("/api/v1/resource/{id}");
        assertThat(info.getMethodsCondition().getMethods()).contains(RequestMethod.GET);

        Method method = methodCaptor.getValue();
        Method expectedMethod = DynamicController.class.getDeclaredMethod("handleGet", HttpServletRequest.class,
                String.class);
        assertThat(method).isEqualTo(expectedMethod);
    }

    @Test
    void shouldRegisterGetEndpointWithoutId() throws NoSuchMethodException {
        Endpoint endpoint = new Endpoint();
        endpoint.setPath("/api/v1/resource");
        endpoint.setMethod(HttpMethod.GET);

        when(dynamicEndpointProperties.getEndpoints()).thenReturn(Collections.singletonList(endpoint));

        dynamicEndpointRegistry.registerEndpoints();

        verify(handlerMapping).registerMapping(requestMappingInfoCaptor.capture(), eq(handler), methodCaptor.capture());

        RequestMappingInfo info = requestMappingInfoCaptor.getValue();
        assertThat(info.getPathPatternsCondition().getPatternValues()).contains("/api/v1/resource");
        assertThat(info.getMethodsCondition().getMethods()).contains(RequestMethod.GET);

        Method method = methodCaptor.getValue();
        Method expectedMethod = DynamicController.class.getDeclaredMethod("handleGetByPage", HttpServletRequest.class,
                Pageable.class);
        assertThat(method).isEqualTo(expectedMethod);
    }

    @Test
    void shouldRegisterPutEndpoint() throws NoSuchMethodException {
        Endpoint endpoint = new Endpoint();
        endpoint.setPath("/api/v1/resource/{id}");
        endpoint.setMethod(HttpMethod.PUT);

        when(dynamicEndpointProperties.getEndpoints()).thenReturn(Collections.singletonList(endpoint));

        dynamicEndpointRegistry.registerEndpoints();

        verify(handlerMapping).registerMapping(requestMappingInfoCaptor.capture(), eq(handler), methodCaptor.capture());

        RequestMappingInfo info = requestMappingInfoCaptor.getValue();
        assertThat(info.getPathPatternsCondition().getPatternValues()).contains("/api/v1/resource/{id}");
        assertThat(info.getMethodsCondition().getMethods()).contains(RequestMethod.PUT);

        Method method = methodCaptor.getValue();
        Method expectedMethod = DynamicController.class.getDeclaredMethod("handlePut", HttpServletRequest.class,
                Resource.class, String.class, String.class);
        assertThat(method).isEqualTo(expectedMethod);
    }
}
