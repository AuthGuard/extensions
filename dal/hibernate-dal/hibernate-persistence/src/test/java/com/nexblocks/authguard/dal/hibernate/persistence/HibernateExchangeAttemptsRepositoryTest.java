package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.model.ExchangeAttemptDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.OffsetDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateExchangeAttemptsRepositoryTest {
    private static final String ENTITY_ID = "account";
    private static final String FROM_EXCHANGE = "basic";

    private HibernateExchangeAttemptsRepository repository;

    private ExchangeAttemptDO firstAttempt;
    private ExchangeAttemptDO secondAttempt;

    @BeforeAll
    void setup() {
        repository = new HibernateExchangeAttemptsRepository();

        firstAttempt = ExchangeAttemptDO.builder()
                .id("first")
                .entityId(ENTITY_ID)
                .exchangeFrom(FROM_EXCHANGE)
                .createdAt(OffsetDateTime.now().minusHours(1))
                .build();

        secondAttempt = ExchangeAttemptDO.builder()
                .id("second")
                .entityId(ENTITY_ID)
                .exchangeFrom("another")
                .createdAt(OffsetDateTime.now())
                .build();

        repository.save(firstAttempt).join();
        repository.save(secondAttempt).join();

        repository.save(ExchangeAttemptDO.builder()
                .id("different-entity")
                .entityId("different")
                .exchangeFrom(FROM_EXCHANGE)
                .createdAt(OffsetDateTime.now())
                .build());
    }

    @Test
    void findByEntity() {
        final Collection<ExchangeAttemptDO> retrieved =
                repository.findByEntity(ENTITY_ID).join();

        assertThat(retrieved).containsExactlyInAnyOrder(firstAttempt, secondAttempt);
    }

    @Test
    void findByEntityAndTimestamp() {
        final Collection<ExchangeAttemptDO> retrieved =
                repository.findByEntityAndTimestamp(ENTITY_ID, OffsetDateTime.now().minusMinutes(30)).join();

        assertThat(retrieved).containsOnly(secondAttempt);
    }

    @Test
    void findByEntityAndTimestampAndExchange() {
        final Collection<ExchangeAttemptDO> retrieved = repository
                .findByEntityAndTimestampAndExchange(ENTITY_ID,
                        OffsetDateTime.now().minusHours(2),
                        FROM_EXCHANGE)
                .join();

        assertThat(retrieved).containsOnly(firstAttempt);
    }
}