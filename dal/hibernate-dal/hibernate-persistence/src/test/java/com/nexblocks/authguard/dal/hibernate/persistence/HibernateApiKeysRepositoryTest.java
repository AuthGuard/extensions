package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.model.ApiKeyDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateApiKeysRepositoryTest {
    private HibernateApiKeysRepository repository;

    @BeforeAll
    void setup() {
        repository = new HibernateApiKeysRepository();
    }

    @Test
    void saveAndGetById() {
        final String id = UUID.randomUUID().toString();

        final ApiKeyDO apiKey = ApiKeyDO.builder()
                .id(id)
                .key("key")
                .build();

        final ApiKeyDO persisted = repository.save(apiKey).join();
        final Optional<ApiKeyDO> retrieved = repository.getById(id).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getByAppId() {
        final String id = UUID.randomUUID().toString();
        final String appId = "app";

        final ApiKeyDO apiKey = ApiKeyDO.builder()
                .id(id)
                .appId(appId)
                .key("getByAppId")
                .build();

        final ApiKeyDO persisted = repository.save(apiKey).join();
        final Collection<ApiKeyDO> retrieved = repository.getByAppId(appId).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getByKey() {
        final String id = UUID.randomUUID().toString();
        final String key = "getByKey";

        final ApiKeyDO apiKey = ApiKeyDO.builder()
                .id(id)
                .appId("get-by-key-app")
                .key(key)
                .build();

        final ApiKeyDO persisted = repository.save(apiKey).join();
        final Optional<ApiKeyDO> retrieved = repository.getByKey(key).join();

        assertThat(retrieved).contains(persisted);
    }
}