package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.ExchangeAttemptDO;
import com.nexblocks.authguard.dal.persistence.ExchangeAttemptsRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateExchangeAttemptsRepository extends AbstractHibernateRepository<ExchangeAttemptDO>
        implements ExchangeAttemptsRepository {
    private static final String GET_BY_ENTITY = "exchange_attempts.getByEntityId";
    private static final String GET_BY_ENTITY_FROM_TIMESTAMP = "exchange_attempts.getByEntityIdFromTimestamp";
    private static final String GET_BY_ALL = "exchange_attempts.getByEntityIdAndExchangeFromTimestamp";

    private static final String ENTITY_ID_FIELD = "entityId";
    private static final String TIMESTAMP_FIELD = "timestamp";
    private static final String FROM_EXCHANGE_FIELD = "exchangeFrom";

    @Inject
    protected HibernateExchangeAttemptsRepository(final ReactiveQueryExecutor queryExecutor) {
        super(ExchangeAttemptDO.class, queryExecutor);
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntity(final long entityId) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ENTITY, ExchangeAttemptDO.class)
                        .setParameter(ENTITY_ID_FIELD, entityId)
                )
                .subscribeAsCompletionStage()
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntityAndTimestamp(final long entityId,
                                                                                     final Instant fromTimestamp) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ENTITY_FROM_TIMESTAMP, ExchangeAttemptDO.class)
                        .setParameter(ENTITY_ID_FIELD, entityId)
                        .setParameter(TIMESTAMP_FIELD, fromTimestamp)
                )
                .subscribeAsCompletionStage()
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntityAndTimestampAndExchange(final long entityId,
                                                                                                final Instant fromTimestamp,
                                                                                                final String fromExchange) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ALL, ExchangeAttemptDO.class)
                        .setParameter(ENTITY_ID_FIELD, entityId)
                        .setParameter(TIMESTAMP_FIELD, fromTimestamp)
                        .setParameter(FROM_EXCHANGE_FIELD, fromExchange)
                )
                .subscribeAsCompletionStage()
                .thenApply(Function.identity());
    }
}
