package com.jinternals.ofs.config.store.services.impl;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import com.jinternals.ofs.config.store.services.DynamicDataService;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicDataServiceImpl implements DynamicDataService {

    private final CouchbaseTemplate couchbaseTemplate;

    public DynamicDataServiceImpl(CouchbaseTemplate couchbaseTemplate) {
        this.couchbaseTemplate = couchbaseTemplate;
    }

    @Override
    public String saveData(String id, String document) {
        JsonObject jsonObject = JsonObject.fromJson(document);
        couchbaseTemplate.getCouchbaseClientFactory().getDefaultCollection().upsert(id, jsonObject);
        return id;
    }

    @Override
    public String getData(String id) {
        return couchbaseTemplate.getCouchbaseClientFactory().getDefaultCollection().get(id).contentAsObject()
                .toString();
    }

    @Override
    public String getData(Pageable pageable) {
        String bucketName = couchbaseTemplate.getBucketName();
        String query = String.format("SELECT t.* FROM `%s` t LIMIT %d OFFSET %d", bucketName, pageable.getPageSize(),
                pageable.getOffset());
        QueryResult result = couchbaseTemplate.getCouchbaseClientFactory().getCluster().query(query);
        List<JsonObject> rows = result.rowsAsObject();
        return JsonArray.from(rows).toString();
    }
}
