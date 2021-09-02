package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class HibernateUserIdentifiersRepository extends AbstractHibernateRepository<UserIdentifierDO> {
    @Inject
    public HibernateUserIdentifiersRepository(final QueryExecutor queryExecutor) {
        super(UserIdentifierDO.class, queryExecutor);
    }

    public CompletableFuture<Void> deleteAll(final Set<UserIdentifierDO> identifiers) {
        return CompletableFuture.allOf(identifiers.stream()
                .map(identifier -> queryExecutor.deleteById(identifier.getId(), UserIdentifierDO.class)).distinct().toArray(CompletableFuture[]::new)
        );
    }

    public List<UserIdentifierDO> getAll() {
        return queryExecutor.getAList(session -> session.createQuery("SELECT iden FROM UserIdentifierDO iden", UserIdentifierDO.class))
                .join();
    }
}
