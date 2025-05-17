package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class HibernateUserIdentifiersRepository extends AbstractHibernateRepository<UserIdentifierDO> {
    @Inject
    public HibernateUserIdentifiersRepository(final ReactiveQueryExecutor queryExecutor) {
        super(UserIdentifierDO.class, queryExecutor);
    }

    public CompletableFuture<Void> deleteAll(final Set<UserIdentifierDO> identifiers) {
        List<Uni<Optional<UserIdentifierDO>>> unis = identifiers.stream()
                .map(identifier -> queryExecutor.deleteById(identifier.getId(), UserIdentifierDO.class))
                .toList();

        if (identifiers.isEmpty()) {
            return CompletableFuture.completedFuture(true)
                    .thenAccept(ignored -> {});
        }

        return Uni.combine().all().unis(unis)
                .with(ignored -> true)
                .subscribeAsCompletionStage()
                .thenAccept(ignored -> {});
    }

    public List<UserIdentifierDO> getAll() {
        return queryExecutor.getAList(session -> session.createQuery("SELECT iden FROM UserIdentifierDO iden", UserIdentifierDO.class))
                .subscribeAsCompletionStage()
                .join();
    }
}
