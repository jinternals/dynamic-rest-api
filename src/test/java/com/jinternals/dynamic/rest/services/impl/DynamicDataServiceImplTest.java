package com.jinternals.dynamic.rest.services.impl;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicDataServiceImplTest {

    @Mock
    private CouchbaseTemplate couchbaseTemplate;

    @Mock
    private CouchbaseClientFactory couchbaseClientFactory;

    @Mock
    private Cluster cluster;

    @Mock
    private Collection collection;

    @Mock
    private QueryResult queryResult;

    @InjectMocks
    private DynamicDataServiceImpl dynamicDataService;

    @Test
    void shouldSaveData() {
        String id = "test-id";
        String document = "{\"key\":\"value\"}";
        when(couchbaseTemplate.getCouchbaseClientFactory()).thenReturn(couchbaseClientFactory);
        when(couchbaseClientFactory.getDefaultCollection()).thenReturn(collection);

        String result = dynamicDataService.saveData(id, document);

        assertThat(result).isEqualTo(id);
        verify(collection).upsert(id, JsonObject.fromJson(document));
    }

    @Test
    void shouldGetData() {
        String id = "test-id";
        String content = "{\"key\":\"value\"}";
        GetResult getResult = mock(GetResult.class);

        when(couchbaseTemplate.getCouchbaseClientFactory()).thenReturn(couchbaseClientFactory);
        when(couchbaseClientFactory.getDefaultCollection()).thenReturn(collection);
        when(collection.get(id)).thenReturn(getResult);
        when(getResult.contentAsObject()).thenReturn(JsonObject.fromJson(content));

        String result = dynamicDataService.getData(id);

        assertThat(result).isEqualTo(content);
    }

    @Test
    void shouldGetDataPageableSorted() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        String bucketName = "test-bucket";
        String query = "SELECT t.* FROM `test-bucket` t ORDER BY t.`name` ASC LIMIT 10 OFFSET 0";
        JsonObject row = JsonObject.create().put("name", "test");
        List<JsonObject> rows = Collections.singletonList(row);

        when(couchbaseTemplate.getBucketName()).thenReturn(bucketName);
        when(couchbaseTemplate.getCouchbaseClientFactory()).thenReturn(couchbaseClientFactory);
        when(couchbaseClientFactory.getCluster()).thenReturn(cluster);
        when(cluster.query(query)).thenReturn(queryResult);
        when(queryResult.rowsAsObject()).thenReturn(rows);

        String result = dynamicDataService.getData(pageable);

        assertThat(result).isEqualTo("[{\"name\":\"test\"}]");
    }

    @Test
    void shouldGetDataPageableUnSorted() {
        Pageable pageable = PageRequest.of(0, 10);
        String bucketName = "test-bucket";
        String query = "SELECT t.* FROM `test-bucket` t LIMIT 10 OFFSET 0";
        JsonObject row = JsonObject.create().put("name", "test");
        List<JsonObject> rows = Collections.singletonList(row);

        when(couchbaseTemplate.getBucketName()).thenReturn(bucketName);
        when(couchbaseTemplate.getCouchbaseClientFactory()).thenReturn(couchbaseClientFactory);
        when(couchbaseClientFactory.getCluster()).thenReturn(cluster);
        when(cluster.query(query)).thenReturn(queryResult);
        when(queryResult.rowsAsObject()).thenReturn(rows);

        String result = dynamicDataService.getData(pageable);

        assertThat(result).isEqualTo("[{\"name\":\"test\"}]");
    }
}
