package com.jinternals.dynamic.rest.handlers;

import com.jinternals.dynamic.rest.configuration.DynamicEndpointProperties;
import com.jinternals.dynamic.rest.configuration.DynamicEndpointProperties.Endpoint;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaArgumentResolverTest {

    @Mock
    private DynamicEndpointProperties dynamicEndpointProperties;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private NativeWebRequest nativeWebRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private WebDataBinderFactory binderFactory;

    @InjectMocks
    private SchemaArgumentResolver schemaArgumentResolver;

    @Test
    void shouldSupportParameterWhenTypeIsResource() {
        doReturn(Resource.class).when(methodParameter).getParameterType();
        assertThat(schemaArgumentResolver.supportsParameter(methodParameter)).isTrue();
    }

    @Test
    void shouldNotSupportParameterWhenTypeIsNotResource() {
        doReturn(String.class).when(methodParameter).getParameterType();
        assertThat(schemaArgumentResolver.supportsParameter(methodParameter)).isFalse();
    }

    @Test
    void shouldResolveArgumentWhenEndpointMatches() throws Exception {
        Endpoint endpoint = new Endpoint();
        endpoint.setPath("/api/v1/config");
        Resource mockResource = mock(Resource.class);
        endpoint.setSchema(mockResource);

        webRequest(nativeWebRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/v1/config");
        when(dynamicEndpointProperties.getEndpoints()).thenReturn(List.of(endpoint));

        Object result = schemaArgumentResolver.resolveArgument(methodParameter, mavContainer, nativeWebRequest,
                binderFactory);

        assertThat(result).isEqualTo(mockResource);
    }

    @Test
    void shouldReturnNullWhenEndpointDoesNotMatch() throws Exception {
        Endpoint endpoint = new Endpoint();
        endpoint.setPath("/api/v1/config");
        Resource mockResource = mock(Resource.class);
        endpoint.setSchema(mockResource);

        webRequest(nativeWebRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/v1/other");
        when(dynamicEndpointProperties.getEndpoints()).thenReturn(List.of(endpoint));

        Object result = schemaArgumentResolver.resolveArgument(methodParameter, mavContainer, nativeWebRequest,
                binderFactory);

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullWhenRequestIsNotHttpServletRequest() throws Exception {
        when(nativeWebRequest.getNativeRequest()).thenReturn(new Object());

        Object result = schemaArgumentResolver.resolveArgument(methodParameter, mavContainer, nativeWebRequest,
                binderFactory);

        assertThat(result).isNull();
    }

    private void webRequest(NativeWebRequest webRequest) {
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
    }
}
