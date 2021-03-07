package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.RoleDO;
import com.nexblocks.authguard.dal.persistence.RolesRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateRolesRepository extends AbstractHibernateRepository<RoleDO>
        implements RolesRepository {
    private static final String GET_BY_ID = "roles.getById";
    private static final String GET_ALL = "roles.getAll";
    private static final String GET_BY_NAME = "roles.getByName";
    private static final String GET_MULTIPLE = "roles.getMultiple";

    private static final String NAME_FIELD = "name";
    private static final String NAMES_FIELD = "names";

    public HibernateRolesRepository() {
        super(RoleDO.class);
    }

    @Override
    public CompletableFuture<Optional<RoleDO>> getById(final String id) {
        return QueryExecutor
                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, RoleDO.class)
                        .setParameter(CommonFields.ID, id))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<RoleDO>> getAll() {
        return QueryExecutor.getAList(session -> session.createNamedQuery(GET_ALL, RoleDO.class))
                .thenApply(list -> list);
    }

    @Override
    public CompletableFuture<Optional<RoleDO>> getByName(final String role) {
        return QueryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_NAME, RoleDO.class)
                .setParameter(NAME_FIELD, role));
    }

    @Override
    public CompletableFuture<Collection<RoleDO>> getMultiple(final Collection<String> roles) {
        return QueryExecutor.getAList(session -> session.createNamedQuery(GET_MULTIPLE, RoleDO.class)
                .setParameter(NAMES_FIELD, roles))
                .thenApply(Function.identity());
    }
}
