package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.ExchangeAttemptDO;
import com.nexblocks.authguard.dal.persistence.ExchangeAttemptsRepository;

import java.time.OffsetDateTime;
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

    protected HibernateExchangeAttemptsRepository() {
        super(ExchangeAttemptDO.class);
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntity(final String entityId) {
        return QueryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ENTITY, ExchangeAttemptDO.class)
                        .setParameter(ENTITY_ID_FIELD, entityId)
                ).thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntityAndTimestamp(final String entityId,
                                                                                     final OffsetDateTime fromTimestamp) {
        return QueryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ENTITY_FROM_TIMESTAMP, ExchangeAttemptDO.class)
                        .setParameter(ENTITY_ID_FIELD, entityId)
                        .setParameter(TIMESTAMP_FIELD, fromTimestamp)
                ).thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<ExchangeAttemptDO>> findByEntityAndTimestampAndExchange(final String entityId,
                                                                                                final OffsetDateTime fromTimestamp,
                                                                                                final String fromExchange) {
        return QueryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ALL, ExchangeAttemptDO.class)
                        .setParameter(ENTITY_ID_FIELD, entityId)
                        .setParameter(TIMESTAMP_FIELD, fromTimestamp)
                        .setParameter(FROM_EXCHANGE_FIELD, fromExchange)
                ).thenApply(Function.identity());
    }
}
