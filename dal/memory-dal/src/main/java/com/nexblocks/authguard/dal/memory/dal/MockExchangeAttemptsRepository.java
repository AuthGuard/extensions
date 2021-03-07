package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.ExchangeAttemptDO;
import com.nexblocks.authguard.dal.persistence.ExchangeAttemptsRepository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockExchangeAttemptsRepository extends AbstractRepository<ExchangeAttemptDO> implements ExchangeAttemptsRepository {
    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntity(final String entityId) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .filter(attempt -> attempt.getEntityId().equals(entityId))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntityAndTimestamp(final String entityId,
                                                                                     final OffsetDateTime fromTimestamp) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .filter(attempt -> attempt.getEntityId().equals(entityId))
                .filter(attempt -> attempt.getCreatedAt().isAfter(fromTimestamp))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntityAndTimestampAndExchange(final String entityId,
                                                                                                final OffsetDateTime fromTimestamp,
                                                                                                final String exchangeFrom) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .filter(attempt -> attempt.getEntityId().equals(entityId))
                .filter(attempt -> attempt.getCreatedAt().isAfter(fromTimestamp))
                .filter(attempt -> attempt.getExchangeFrom().equals(exchangeFrom))
                .collect(Collectors.toList()));
    }
}
