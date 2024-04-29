package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AppDO;
import com.nexblocks.authguard.dal.persistence.ApplicationsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

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
    public CompletableFuture<List<AppDO>> getAllForAccount(final long accountId, final Page<Long> page) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(app -> app.getParentAccountId().equals(accountId) && app.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }
}
