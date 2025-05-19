package com.nexblocks.authguard.dal.hibernate.common;

import io.smallrye.mutiny.Uni;

import java.util.Optional;

public abstract class AbstractHibernateRepository<T> {
    private final Class<T> entityType;

    /*
     * TODO replace this.
     *  It was added as a quick hack to support passing configuration from ConfigContext. We need
     *  a better way.
     */
    protected final ReactiveQueryExecutor queryExecutor;

    protected AbstractHibernateRepository(final Class<T> entityType, final ReactiveQueryExecutor queryExecutor) {
        this.entityType = entityType;
        this.queryExecutor = queryExecutor;
    }

    public Uni<T> save(final T entity) {
        return queryExecutor.persistAndReturn(entity);
    }
    
    public Uni<Optional<T>> getById(final long id) {
        return queryExecutor.getById(id, entityType);
    }

    public Uni<Optional<T>> update(final T entity) {
        return queryExecutor.updateAndReturn(entity);
    }

    public Uni<Optional<T>> delete(final long id) {
        return queryExecutor.deleteById(id, entityType);
    }
}
