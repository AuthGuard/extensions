package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.persistence.PermissionsRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernatePermissionsRepository extends AbstractHibernateRepository<PermissionDO>
        implements PermissionsRepository {
    private static final String GET_BY_ID = "permissions.getById";
    private static final String GET_ALL = "permissions.getAll";
    private static final String GET_BY_GROUP_AND_NAME = "permissions.getByGroupAndName";
    private static final String GET_BY_GROUP = "permissions.getByGroup";

    private static final String GROUP_FIELD = "group";
    private static final String NAME_FIELD = "name";

    public HibernatePermissionsRepository() {
        super(PermissionDO.class);
    }

    @Override
    public CompletableFuture<Optional<PermissionDO>> getById(final String id) {
        return QueryExecutor
                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, PermissionDO.class)
                        .setParameter(CommonFields.ID, id))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Optional<PermissionDO>> search(final String group, final String name) {
        return QueryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_GROUP_AND_NAME, PermissionDO.class)
                .setParameter(GROUP_FIELD, group)
                .setParameter(NAME_FIELD, name));
    }

    @Override
    public CompletableFuture<Collection<PermissionDO>> getAll() {
        return QueryExecutor.getAList(session -> session.createNamedQuery(GET_ALL, PermissionDO.class))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<PermissionDO>> getAllForGroup(final String group) {
        return QueryExecutor.getAList(session -> session.createNamedQuery(GET_BY_GROUP, PermissionDO.class)
                .setParameter(GROUP_FIELD, group))
                .thenApply(Function.identity());
    }
}
