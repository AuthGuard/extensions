package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.ApiKeyDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateApiKeysRepositoryTest {
    private HibernateApiKeysRepository repository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateApiKeysRepository(new ReactiveQueryExecutor(sessionProvider));
    }

    @Test
    public void saveAndGetById() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        final ApiKeyDO apiKey = ApiKeyDO.builder()
                .id(id)
                .key("saveAndGetById")
                .build();

        final ApiKeyDO persisted = repository.save(apiKey)
                .subscribeAsCompletionStage().join();
        final Optional<ApiKeyDO> retrieved = repository.getById(id)
                .subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void getByAppId() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        final long appId = 201;

        final ApiKeyDO apiKey = ApiKeyDO.builder()
                .id(id)
                .appId(appId)
                .key("getByAppId")
                .build();

        final ApiKeyDO persisted = repository.save(apiKey)
                .subscribeAsCompletionStage().join();
        final Collection<ApiKeyDO> retrieved = repository.getByAppId(appId)
                .subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void getByKey() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        final String key = "getByKey";

        final ApiKeyDO apiKey = ApiKeyDO.builder()
                .id(id)
                .appId(203)
                .key(key)
                .build();

        final ApiKeyDO persisted = repository.save(apiKey)
                .subscribeAsCompletionStage().join();
        final Optional<ApiKeyDO> retrieved = repository.getByKey(key)
                .subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndDeleteById() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        final ApiKeyDO apiKey = ApiKeyDO.builder()
                .id(id)
                .key("saveAndDeleteById")
                .build();

        final ApiKeyDO persisted = repository.save(apiKey)
                .subscribeAsCompletionStage().join();
        final Optional<ApiKeyDO> deleted = repository.delete(id)
                .subscribeAsCompletionStage().join();

        assertThat(deleted).contains(persisted);
    }
}