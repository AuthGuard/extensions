package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.IdempotentRecordDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateIdempotencyRecordRepositoryTest {
    private HibernateIdempotencyRecordRepository repository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateIdempotencyRecordRepository(new QueryExecutor(sessionProvider));
    }

    @Test
    public void saveAndGetById() {
        final String id = "record-id";

        final IdempotentRecordDO persisted = repository.save(IdempotentRecordDO.builder()
                .id(id)
                .entityId("entity-id")
                .entityType("entity")
                .idempotentKey("key")
                .build()).join();

        final Optional<IdempotentRecordDO> retrieved = repository.getById(id).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndGetByKey() {
        final String key = "by-key-key";

        final IdempotentRecordDO persisted = repository.save(IdempotentRecordDO.builder()
                .id("by-key-id")
                .entityId("by-key-entity-id")
                .entityType("by-key-entity")
                .idempotentKey(key)
                .build()).join();

        final List<IdempotentRecordDO> retrieved = repository.findByKey(key).join();

        assertThat(retrieved).containsOnly(persisted);
    }

    @Test
    public void saveAndGetByKeyAndEntityType() {
        final String key = "by-key-and-entity-key";
        final String entityType = "by-key-and-entity-entity";

        final IdempotentRecordDO persisted = repository.save(IdempotentRecordDO.builder()
                .id("by-key-and-entity-id")
                .entityId("by-key-and-entity-entity-id")
                .entityType(entityType)
                .idempotentKey(key)
                .build()).join();

        final Optional<IdempotentRecordDO> retrieved = repository.findByKeyAndEntityType(key, entityType).join();

        assertThat(retrieved).contains(persisted);
    }
}