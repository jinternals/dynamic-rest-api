package com.jinternals.dynamic.rest.services;


import org.springframework.data.domain.Pageable;

public interface DynamicDataService {
    String saveData(String id, String document);
    String getData(String id);
    String getData(Pageable pageable);
}
