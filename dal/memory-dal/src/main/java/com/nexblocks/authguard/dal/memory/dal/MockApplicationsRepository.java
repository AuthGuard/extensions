package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AppDO;
import com.nexblocks.authguard.dal.persistence.ApplicationsRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockApplicationsRepository extends AbstractRepository<AppDO> implements ApplicationsRepository {
    @Override
    public CompletableFuture<Optional<AppDO>> getByExternalId(final String externalId) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(app -> app.getExternalId() != null)
                .filter(app -> app.getExternalId().equals(externalId))
                .findFirst());
    }

    @Override
    public CompletableFuture<List<AppDO>> getAllForAccount(final String accountId) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(app -> app.getParentAccountId().equals(accountId))
                .collect(Collectors.toList()));
    }
}
