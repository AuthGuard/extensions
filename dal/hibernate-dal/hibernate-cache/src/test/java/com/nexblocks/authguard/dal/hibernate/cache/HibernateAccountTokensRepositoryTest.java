package com.nexblocks.authguard.dal.hibernate.cache;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.AccountTokenDO;
import com.nexblocks.authguard.dal.model.TokenRestrictionsDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateAccountTokensRepositoryTest {
    private HibernateAccountTokensRepository repository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateAccountTokensRepository(new ReactiveQueryExecutor(sessionProvider));
    }

    @Test
    public void saveAndGetById() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        final AccountTokenDO accountToken = AccountTokenDO.builder()
                .id(id)
                .associatedAccountId(101)
                .token("token")
                .expiresAt(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .additionalInformation(Collections.emptyMap())
                .tokenRestrictions(TokenRestrictionsDO.builder()
                        .permissions(Collections.emptySet())
                        .scopes(Collections.emptySet())
                        .build())
                .build();

        final AccountTokenDO persisted = repository.save(accountToken).subscribeAsCompletionStage().join();
        final Optional<AccountTokenDO> retrieved = repository.getById(id).subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getByToken() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        final String token = "getByToken-token";

        final AccountTokenDO accountToken = AccountTokenDO.builder()
                .id(id)
                .associatedAccountId(101)
                .token(token)
                .expiresAt(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .additionalInformation(Collections.emptyMap())
                .tokenRestrictions(TokenRestrictionsDO.builder()
                        .permissions(Collections.emptySet())
                        .scopes(Collections.emptySet())
                        .build())
                .build();

        final AccountTokenDO persisted = repository.save(accountToken).subscribeAsCompletionStage().join();
        final Optional<AccountTokenDO> retrieved = repository.getByToken(token).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getByTokenNonExistent() {
        assertThat(repository.getByToken("nothing").join()).isEmpty();
    }
}