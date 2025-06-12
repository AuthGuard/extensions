package com.nexblocks.authguard.dal.hibernate.common;

import com.google.inject.Inject;
import org.hibernate.Session;
import org.hibernate.query.Query;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import io.smallrye.mutiny.Uni;
import java.util.function.Consumer;
import java.util.function.Function;

public class QueryExecutor {

    private final SessionProvider sessionProvider;

    @Inject
    public QueryExecutor(final SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public <T> Uni<T> persistAndReturn(final T entity) {
        return doInNewTransaction(session -> session.persist(entity))
                .map(ignored -> entity);
    }

    public <T> Uni<Optional<T>> updateAndReturn(final T entity) {
        return doInNewTransaction(session -> session.update(entity))
                .map(ignored -> Optional.of(entity));
    }

    public <T> Uni<Optional<T>> getById(final String id, final Class<T> entityType) {
        return inNewTransaction(session -> session.get(entityType, id))
                .map(Optional::ofNullable);
    }

    public <T> Uni<Optional<T>> getById(final Long id, final Class<T> entityType) {
        return inNewTransaction(session -> session.get(entityType, id))
                .map(Optional::ofNullable);
    }

    public <T> Uni<Optional<T>> getSingleResult(final Function<Session, Query<T>> sessionQuery) {
        return inNewTransaction(session -> {
            final Query<T> query = sessionQuery.apply(session);

            try {
                return Optional.of(query.getSingleResult());
            } catch (final NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public <T> Uni<List<T>> getAList(final Function<Session, Query<T>> sessionQuery) {
        return inNewTransaction(session -> {
            final Query<T> query = sessionQuery.apply(session);
            return query.getResultList();
        });
    }

    public <T> Uni<List<T>> getAList(final Function<Session, Query<T>> sessionQuery,
                                                   final int limit) {
        return inNewTransaction(session -> {
            final Query<T> query = sessionQuery.apply(session);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    public <T> Uni<Optional<T>> deleteById(final String id, final Class<T> entityType) {
        return getById(id, entityType)
                .flatMap(retrieved -> {
                    if (retrieved.isPresent()) {
                        return doInNewTransaction(session -> session.delete(retrieved.get()))
                                .map(ignored -> retrieved);
                    } else {
                        return Uni.createFrom().item(Optional.empty());
                    }
                });
    }

    public <T> Uni<Optional<T>> deleteById(final Long id, final Class<T> entityType) {
        return getById(id, entityType)
                .flatMap(retrieved -> {
                    if (retrieved.isPresent()) {
                        return doInNewTransaction(session -> session.delete(retrieved.get()))
                                .map(ignored -> retrieved);
                    } else {
                        return Uni.createFrom().item(Optional.empty());
                    }
                });
    }

    Uni<Void> doInNewTransaction(final Consumer<Session> consumer) {
        return Uni.createFrom().item(() -> {
            try (Session session = sessionProvider.newBlockingSession()) {
                session.beginTransaction();

                consumer.accept(session);

                session.getTransaction().commit();
            }

            return null;
        });
    }

    <T> Uni<T> inNewTransaction(final Function<Session, T> function) {
        return Uni.createFrom().item(() -> {
            final Session session = sessionProvider.newBlockingSession();

            session.beginTransaction();

            final T entity = function.apply(session);

            session.getTransaction().commit();
            session.close();

            return entity;
        });
    }
}
