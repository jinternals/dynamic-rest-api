package com.jinternals.dynamic.rest.services.impl;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import com.jinternals.dynamic.rest.services.DynamicDataService;
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
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(String.format("SELECT t.* FROM `%s` t", bucketName));

        if (pageable.getSort().isSorted()) {
            String orderBy = pageable.getSort().stream()
                    .map(order -> {
                        String property = order.getProperty();
                        String escapedProperty = java.util.Arrays.stream(property.split("\\."))
                                .map(part -> "`" + part + "`")
                                .collect(java.util.stream.Collectors.joining("."));
                        return String.format("t.%s %s", escapedProperty, order.getDirection());
                    })
                    .collect(java.util.stream.Collectors.joining(", "));
            queryBuilder.append(" ORDER BY ").append(orderBy);
        }

        queryBuilder.append(String.format(" LIMIT %d OFFSET %d", pageable.getPageSize(), pageable.getOffset()));
        QueryResult result = couchbaseTemplate.getCouchbaseClientFactory().getCluster().query(queryBuilder.toString());
        List<JsonObject> rows = result.rowsAsObject();
        return JsonArray.from(rows).toString();
    }
}
