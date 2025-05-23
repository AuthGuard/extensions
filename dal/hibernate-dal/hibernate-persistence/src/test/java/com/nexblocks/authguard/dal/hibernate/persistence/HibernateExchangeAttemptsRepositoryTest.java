package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.ExchangeAttemptDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateExchangeAttemptsRepositoryTest {
    private static final long ENTITY_ID = 101;
    private static final String FROM_EXCHANGE = "basic";

    private HibernateExchangeAttemptsRepository repository;

    private ExchangeAttemptDO firstAttempt;
    private ExchangeAttemptDO secondAttempt;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateExchangeAttemptsRepository(new ReactiveQueryExecutor(sessionProvider));

        firstAttempt = ExchangeAttemptDO.builder()
                .id(1)
                .entityId(ENTITY_ID)
                .exchangeFrom(FROM_EXCHANGE)
                .createdAt(Instant.now().minus(Duration.ofHours(1)))
                .build();

        secondAttempt = ExchangeAttemptDO.builder()
                .id(2)
                .entityId(ENTITY_ID)
                .exchangeFrom("another")
                .createdAt(Instant.now())
                .build();

        repository.save(firstAttempt).subscribeAsCompletionStage().join();
        repository.save(secondAttempt).subscribeAsCompletionStage().join();

        repository.save(ExchangeAttemptDO.builder()
                .id(3)
                .entityId(102)
                .exchangeFrom(FROM_EXCHANGE)
                .createdAt(Instant.now())
                .build());
    }

    @Test
    public void findByEntity() {
        final Collection<ExchangeAttemptDO> retrieved =
                repository.findByEntity(ENTITY_ID).join();

        assertThat(retrieved).containsExactlyInAnyOrder(firstAttempt, secondAttempt);
    }

    @Test
    public void findByEntityAndTimestamp() {
        final Collection<ExchangeAttemptDO> retrieved =
                repository.findByEntityAndTimestamp(ENTITY_ID, Instant.now().minus(Duration.ofMinutes(30))).join();

        assertThat(retrieved).containsOnly(secondAttempt);
    }

    @Test
    public void findByEntityAndTimestampAndExchange() {
        final Collection<ExchangeAttemptDO> retrieved = repository
                .findByEntityAndTimestampAndExchange(ENTITY_ID,
                        Instant.now().minus(Duration.ofHours(2)),
                        FROM_EXCHANGE)
                .join();

        assertThat(retrieved).containsOnly(firstAttempt);
    }
}