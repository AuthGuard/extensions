package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.CryptoKeyDO;
import com.nexblocks.authguard.dal.persistence.Page;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateCryptoKeysRepositoryTests {
    private HibernateCryptoKeysRepository repository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateCryptoKeysRepository(new ReactiveQueryExecutor(sessionProvider));
    }

    @Test
    public void saveAndGetById() {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        CryptoKeyDO key = CryptoKeyDO.builder()
                .id(id)
                .domain("main")
                .accountId(101L)
                .appId(201L)
                .nonce(new byte[] { 1, 2, 3 })
                .createdAt(Instant.now().minusMillis(1))
                .parameters(Map.of("key", "value"))
                .build();

        CryptoKeyDO persisted = repository.save(key).subscribeAsCompletionStage().join();
        Optional<CryptoKeyDO> retrieved = repository.getById(id).subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndGetByAccountId() {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        CryptoKeyDO key = CryptoKeyDO.builder()
                .id(id)
                .domain("main")
                .accountId(200L)
                .appId(201L)
                .nonce(new byte[] { 1, 2, 3 })
                .createdAt(Instant.now().minusMillis(1))
                .parameters(Map.of("key", "value"))
                .build();

        CryptoKeyDO persisted = repository.save(key).subscribeAsCompletionStage().join();
        List<CryptoKeyDO> retrieved = repository.findByAccountId(
                "main", 200L, Page.of(Instant.now().plusSeconds(1), 10, Instant.MAX))
                .subscribeAsCompletionStage().join();

        assertThat(retrieved).containsExactly(persisted);
    }
}
