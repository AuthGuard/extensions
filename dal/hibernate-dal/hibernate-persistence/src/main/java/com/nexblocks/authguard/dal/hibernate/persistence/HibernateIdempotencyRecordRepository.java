package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.IdempotentRecordDO;
import com.nexblocks.authguard.dal.persistence.IdempotentRecordsRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HibernateIdempotencyRecordRepository extends AbstractHibernateRepository<IdempotentRecordDO>
        implements IdempotentRecordsRepository {
    private static final String GET_BY_KEY = "idempotent_records.getByKey";
    private static final String GET_BY_KEY_AND_ENTITY = "idempotent_records.getByKeyAndEntity";

    private static final String KEY_FIELD = "key";
    private static final String ENTITY_TYPE_FIELD = "entityType";

    public HibernateIdempotencyRecordRepository() {
        super(IdempotentRecordDO.class);
    }

    @Override
    public CompletableFuture<List<IdempotentRecordDO>> findByKey(final String key) {
        return QueryExecutor.getAList(session -> session.createNamedQuery(GET_BY_KEY, IdempotentRecordDO.class)
                .setParameter(KEY_FIELD, key));
    }

    @Override
    public CompletableFuture<Optional<IdempotentRecordDO>> findByKeyAndEntityType(final String key, final String entityType) {
        return QueryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_KEY_AND_ENTITY, IdempotentRecordDO.class)
                .setParameter(KEY_FIELD, key)
                .setParameter(ENTITY_TYPE_FIELD, entityType));
    }
}
