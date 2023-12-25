package com.nexblocks.authguard.dal.hibernate.cache;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.AccountLockDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateAccountLocksRepositoryTest {
    private HibernateAccountLocksRepository repository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateAccountLocksRepository(new QueryExecutor(sessionProvider));
    }

    @Test
    public void saveAndGetById() {
        final long id = UUID.randomUUID().getMostSignificantBits();

        final AccountLockDO accountLock = AccountLockDO.builder()
                .id(id)
                .accountId(101)
                .expiresAt(Instant.now())
                .build();

        final AccountLockDO persisted = repository.save(accountLock).join();
        final Optional<AccountLockDO> retrieved = repository.getById(id).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getByToken() {
        final long id = UUID.randomUUID().getMostSignificantBits();

        final AccountLockDO accountLock = AccountLockDO.builder()
                .id(id)
                .accountId(102)
                .expiresAt(Instant.now())
                .build();

        final AccountLockDO persisted = repository.save(accountLock).join();
        final Collection<AccountLockDO> retrieved = repository.findByAccountId(accountLock.getAccountId()).join();

        assertThat(retrieved).containsOnly(persisted);
    }

    @Test
    void getByTokenNonExistent() {
        assertThat(repository.findByAccountId(0).join()).isEmpty();
    }

}