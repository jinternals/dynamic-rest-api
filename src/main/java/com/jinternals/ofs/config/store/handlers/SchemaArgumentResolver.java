package com.jinternals.ofs.config.store.handlers;

import com.jinternals.ofs.config.store.configuration.DynamicEndpointProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@RequiredArgsConstructor
public class SchemaArgumentResolver implements HandlerMethodArgumentResolver {

    private static PathMatcher pathMatcher = new AntPathMatcher();
    private final DynamicEndpointProperties dynamicEndpointProperties;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Resource.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        if (webRequest.getNativeRequest() instanceof HttpServletRequest request) {
            String requestURI = request.getRequestURI();
            Optional<DynamicEndpointProperties.Endpoint> endpointProperties = dynamicEndpointProperties
                    .getEndpoints()
                    .stream()
                    .filter(endpoint -> pathMatcher.match(endpoint.getPath(), requestURI))
                    .findFirst();

            if (endpointProperties.isPresent()) {
                return endpointProperties.get().getSchema();
            }
        }

        return null;
    }

}
