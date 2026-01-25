package com.jinternals.dynamic.rest.controllers;

import com.jinternals.dynamic.rest.configuration.DynamicEndpointProperties.Endpoint;

interface EndpointRegistrar {
    void register(Endpoint endpoint);
}
