package com.nexblocks.authguard.dal.hibernate.common;

import com.nexblocks.authguard.dal.repository.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractHibernateRepository<T> implements Repository<T> {
    private final Class<T> entityType;

    /*
     * TODO replace this.
     *  It was added as a quick hack to support passing configuration from ConfigContext. We need
     *  a better way.
     */
    protected final QueryExecutor queryExecutor;

    protected AbstractHibernateRepository(final Class<T> entityType, final QueryExecutor queryExecutor) {
        this.entityType = entityType;
        this.queryExecutor = queryExecutor;
    }

    public CompletableFuture<T> save(final T entity) {
        return queryExecutor.persistAndReturn(entity);
    }
    
    public CompletableFuture<Optional<T>> getById(final String id) {
        return queryExecutor.getById(id, entityType);
    }

    public CompletableFuture<Optional<T>> update(final T entity) {
        return queryExecutor.updateAndReturn(entity);
    }

    public CompletableFuture<Optional<T>> delete(final String id) {
        return queryExecutor.deleteById(id, entityType);
    }
}
