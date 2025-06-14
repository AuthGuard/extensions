package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.ExchangeAttemptDO;
import com.nexblocks.authguard.dal.persistence.ExchangeAttemptsRepository;

import java.time.Instant;
import java.util.Collection;
import io.smallrye.mutiny.Uni;
import java.util.stream.Collectors;

@Singleton
public class MockExchangeAttemptsRepository extends AbstractRepository<ExchangeAttemptDO> implements ExchangeAttemptsRepository {
    @Override
    public Uni<Collection<ExchangeAttemptDO>> findByEntity(final long entityId) {
        return Uni.createFrom().item(getRepo().values()
                .stream()
                .filter(attempt -> attempt.getEntityId() == entityId)
                .collect(Collectors.toList()));
    }

    @Override
    public Uni<Collection<ExchangeAttemptDO>> findByEntityAndTimestamp(final long entityId,
                                                                                     final Instant fromTimestamp) {
        return Uni.createFrom().item(getRepo().values()
                .stream()
                .filter(attempt -> attempt.getEntityId() == entityId)
                .filter(attempt -> attempt.getCreatedAt().isAfter(fromTimestamp))
                .collect(Collectors.toList()));
    }

    @Override
    public Uni<Collection<ExchangeAttemptDO>> findByEntityAndTimestampAndExchange(final long entityId,
                                                                                                final Instant fromTimestamp,
                                                                                                final String exchangeFrom) {
        return Uni.createFrom().item(getRepo().values()
                .stream()
                .filter(attempt -> attempt.getEntityId() == entityId)
                .filter(attempt -> attempt.getCreatedAt().isAfter(fromTimestamp))
                .filter(attempt -> attempt.getExchangeFrom().equals(exchangeFrom))
                .collect(Collectors.toList()));
    }
}
