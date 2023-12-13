package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.ExchangeAttemptDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.ExchangeAttemptsRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MongoExchangeAttemptsRepository extends AbstractMongoRepository<ExchangeAttemptDO>
        implements ExchangeAttemptsRepository {
    private static final String COLLECTION_KEY = "exchange_attempts";

    @Inject
    public MongoExchangeAttemptsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.EXCHANGE_ATTEMPTS,
                ExchangeAttemptDO.class);
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntity(final long entityId) {
        return facade.find(Filters.eq("entityId", entityId))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntityAndTimestamp
            (final long entityId,
             final Instant fromTimestamp) {
        return facade.find(Filters.and(
                Filters.eq("entityId", entityId),
                Filters.gte("createdAt", fromTimestamp)
        )).thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntityAndTimestampAndExchange
            (final long entityId,
             final Instant fromTimestamp,
             final String fromExchange) {
        return facade.find(Filters.and(
                Filters.eq("entityId", entityId),
                Filters.gte("createdAt", fromTimestamp),
                Filters.eq("exchangeFrom", fromExchange)
        )).thenApply(Function.identity());
    }
}
