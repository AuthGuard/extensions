package com.nexblocks.authguard.dal.hibernate.common;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ReactiveQueryExecutor {
    private final Mutiny.SessionFactory sessionFactory;

    @Inject
    public ReactiveQueryExecutor(final Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Inject
    public ReactiveQueryExecutor(final SessionProvider sessionProvider) {
        this.sessionFactory = sessionProvider.getFactory();
    }

    public <T> Uni<Optional<T>> getById(Long id, Class<T> type) {
        return sessionFactory.withSession(session ->
                session.find(type, id).map(Optional::ofNullable)
        );
    }

    public <T> Uni<T> persistAndReturn(T entity) {
        return sessionFactory.withTransaction((session, tx) ->
                session.persist(entity).replaceWith(entity)
        );
    }

    public <T> Uni<Optional<T>> updateAndReturn(T entity) {
        return sessionFactory.withTransaction((session, tx) ->
                session.merge(entity).map(Optional::of)
        );
    }

    public <T> Uni<Optional<T>> deleteById(Long id, Class<T> type) {
        return sessionFactory.withTransaction((session, tx) ->
                session.find(type, id).flatMap(existing ->
                        existing == null ? Uni.createFrom().item(Optional.empty()) :
                                session.remove(existing).replaceWith(Optional.of(existing))
                )
        );
    }

    public <T> Uni<List<T>> runQuery(String hql, Class<T> resultType, Map<String, Object> params) {
        return sessionFactory.withSession(session -> {
            var query = session.createQuery(hql, resultType);
            params.forEach(query::setParameter);
            return query.getResultList();
        });
    }

    // Named query: single result
    public <T> Uni<Optional<T>> getSingleResult(Function<Mutiny.Session, Mutiny.SelectionQuery<T>> queryFunction) {
        return sessionFactory.withSession(session -> {
            Mutiny.SelectionQuery<T> query = queryFunction.apply(session);
            return query.getResultList()
                    .onItem().transform(list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(0)));
        });
    }
    public <T> Uni<Optional<T>> getSingleResult(String queryName, Class<T> resultClass, Map<String, Object> parameters) {
        return sessionFactory.withSession(session -> {
            Mutiny.SelectionQuery<T> query = session.createNamedQuery(queryName, resultClass);
            parameters.forEach(query::setParameter);
            return query.getResultList()
                    .map(list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(0)));
        });
    }

    public <T, P1> Uni<Optional<T>> getSingleResult(String queryName, Class<T> resultClass,
                                                    String param1, P1 value1) {
        return sessionFactory.withSession(session ->
                session.createNamedQuery(queryName, resultClass)
                        .setParameter(param1, value1)
                        .getSingleResultOrNull()
        ).map(Optional::ofNullable);
    }

    public <T> Uni<Optional<T>> getSingleResult(String queryName, Class<T> resultClass,
                                                        String param1, Object value1,
                                                        String param2, Object value2) {
        return sessionFactory.withSession(session ->
                session.createNamedQuery(queryName, resultClass)
                        .setParameter(param1, value1)
                        .setParameter(param2, value2)
                        .getSingleResultOrNull()
        ).map(Optional::ofNullable);
    }


    // Named query: list
    public <T> Uni<List<T>> getAList(Function<Mutiny.Session, Mutiny.SelectionQuery<T>> queryFunction) {
        return sessionFactory.withSession(session -> queryFunction.apply(session).getResultList());
    }

    public <T> Uni<List<T>> getAList(String queryName, Class<T> resultClass, Map<String, Object> parameters) {
        return sessionFactory.withSession(session -> {
            Mutiny.SelectionQuery<T> query = session.createNamedQuery(queryName, resultClass);
            parameters.forEach(query::setParameter);
            return query.getResultList();
        });
    }

    public <T> Uni<List<T>> getAList(String queryName, Class<T> resultClass) {
        return sessionFactory.withSession(session ->
                session.createNamedQuery(queryName, resultClass)
                        .getResultList()
        );
    }

    public <T> Uni<List<T>> getAList(String queryName, Class<T> resultClass,
                                         String param1, Object value1) {
        return sessionFactory.withSession(session ->
                session.createNamedQuery(queryName, resultClass)
                        .setParameter(param1, value1)
                        .getResultList()
        );
    }

    public <T> Uni<List<T>> getAList(String queryName, Class<T> resultClass,
                                             String param1, Object value1,
                                             String param2, Object value2) {
        return sessionFactory.withSession(session ->
                session.createNamedQuery(queryName, resultClass)
                        .setParameter(param1, value1)
                        .setParameter(param2, value2)
                        .getResultList()
        );
    }

    public <T> Uni<List<T>> getAList(String queryName, Class<T> resultClass,
                                                 String param1, Object value1,
                                                 String param2, Object value2,
                                                 String param3, Object value3) {
        return sessionFactory.withSession(session ->
                session.createNamedQuery(queryName, resultClass)
                        .setParameter(param1, value1)
                        .setParameter(param2, value2)
                        .setParameter(param3, value3)
                        .getResultList()
        );
    }

    // Named query: list with limit
    public <T> Uni<List<T>> getAList(String queryName, Class<T> resultClass, Map<String, Object> parameters, int limit) {
        return sessionFactory.withSession(session -> {
            Mutiny.SelectionQuery<T> query = session.createNamedQuery(queryName, resultClass);
            parameters.forEach(query::setParameter);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    public <T> Uni<List<T>> getAList(Function<Mutiny.Session, Mutiny.SelectionQuery<T>> queryFunction, int limit) {
        return sessionFactory.withSession(session -> {
            Mutiny.SelectionQuery<T> query = queryFunction.apply(session);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }
}

