package com.jinternals.ofs.config.store.services.impl;


import com.jinternals.ofs.config.store.services.DynamicDataService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DynamicDataServiceImpl implements DynamicDataService {

    @Override
    public String saveData(String id, String document) {
        return "";
    }

    @Override
    public String getData(String id) {
        return "";
    }

    @Override
    public String getData(Pageable pageable) {
        return "";
    }
}
