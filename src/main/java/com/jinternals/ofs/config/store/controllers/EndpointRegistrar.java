package com.jinternals.ofs.config.store.controllers;

import com.jinternals.ofs.config.store.configuration.DynamicEndpointProperties.Endpoint;

interface EndpointRegistrar {
    void register(Endpoint endpoint);
}
