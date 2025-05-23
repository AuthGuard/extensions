package com.nexblocks.authguard.dal.hibernate.common;

import com.google.inject.Inject;
import org.hibernate.Session;
import org.hibernate.query.Query;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class QueryExecutor {

    private final SessionProvider sessionProvider;

    @Inject
    public QueryExecutor(final SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public <T> CompletableFuture<T> persistAndReturn(final T entity) {
        return doInNewTransaction(session -> session.persist(entity))
                .thenApply(ignored -> entity);
    }

    public <T> CompletableFuture<Optional<T>> updateAndReturn(final T entity) {
        return doInNewTransaction(session -> session.update(entity))
                .thenApply(ignored -> Optional.of(entity));
    }

    public <T> CompletableFuture<Optional<T>> getById(final String id, final Class<T> entityType) {
        return inNewTransaction(session -> session.get(entityType, id))
                .thenApply(Optional::ofNullable);
    }

    public <T> CompletableFuture<Optional<T>> getById(final Long id, final Class<T> entityType) {
        return inNewTransaction(session -> session.get(entityType, id))
                .thenApply(Optional::ofNullable);
    }

    public <T> CompletableFuture<Optional<T>> getSingleResult(final Function<Session, Query<T>> sessionQuery) {
        return inNewTransaction(session -> {
            final Query<T> query = sessionQuery.apply(session);

            try {
                return Optional.of(query.getSingleResult());
            } catch (final NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public <T> CompletableFuture<List<T>> getAList(final Function<Session, Query<T>> sessionQuery) {
        return inNewTransaction(session -> {
            final Query<T> query = sessionQuery.apply(session);
            return query.getResultList();
        });
    }

    public <T> CompletableFuture<List<T>> getAList(final Function<Session, Query<T>> sessionQuery,
                                                   final int limit) {
        return inNewTransaction(session -> {
            final Query<T> query = sessionQuery.apply(session);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    public <T> CompletableFuture<Optional<T>> deleteById(final String id, final Class<T> entityType) {
        return getById(id, entityType)
                .thenCompose(retrieved -> {
                    if (retrieved.isPresent()) {
                        return doInNewTransaction(session -> session.delete(retrieved.get()))
                                .thenApply(ignored -> retrieved);
                    } else {
                        return CompletableFuture.completedFuture(Optional.empty());
                    }
                });
    }

    public <T> CompletableFuture<Optional<T>> deleteById(final Long id, final Class<T> entityType) {
        return getById(id, entityType)
                .thenCompose(retrieved -> {
                    if (retrieved.isPresent()) {
                        return doInNewTransaction(session -> session.delete(retrieved.get()))
                                .thenApply(ignored -> retrieved);
                    } else {
                        return CompletableFuture.completedFuture(Optional.empty());
                    }
                });
    }

    CompletableFuture<Void> doInNewTransaction(final Consumer<Session> consumer) {
        return CompletableFuture.runAsync(() -> {

            try (Session session = sessionProvider.newBlockingSession()) {
                session.beginTransaction();

                consumer.accept(session);

                session.getTransaction().commit();
            }
        });
    }

    <T> CompletableFuture<T> inNewTransaction(final Function<Session, T> function) {
        return CompletableFuture.supplyAsync(() -> {
            final Session session = sessionProvider.newBlockingSession();

            session.beginTransaction();

            final T entity = function.apply(session);

            session.getTransaction().commit();
            session.close();

            return entity;
        });
    }
}
