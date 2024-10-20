package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.TotpKeyDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateTotpKeysRepositoryTest {
    private HibernateTotpKeysRepository repository;

    private TotpKeyDO first;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateTotpKeysRepository(new QueryExecutor(sessionProvider));

        first = repository.save(TotpKeyDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .domain("main")
                .accountId(1L)
                .encryptedKey(new byte[] { 1, 2, 3 })
                .nonce(new byte[] { 4, 5, 6 })
                .build()).join();

        repository.save(TotpKeyDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .domain("main")
                .accountId(2L)
                .build()).join();
    }

    @Test
    public void getById() {
        List<TotpKeyDO> result = repository.findByAccountId("main", 1L).join();

        assertThat(result).containsOnly(first);
        assertThat(result.get(0).getEncryptedKey()).containsExactly(1, 2, 3);
        assertThat(result.get(0).getNonce()).containsExactly(4, 5, 6);
    }
}