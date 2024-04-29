package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.ClientDO;
import com.nexblocks.authguard.dal.persistence.ClientsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockClientsRepository extends AbstractRepository<ClientDO> implements ClientsRepository {
    @Override
    public CompletableFuture<Optional<ClientDO>> getByExternalId(final String externalId) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(app -> app.getExternalId() != null)
                .filter(app -> app.getExternalId().equals(externalId))
                .findFirst());
    }

    @Override
    public CompletableFuture<List<ClientDO>> getAllForAccount(final long accountId, final Page<Long> page) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(client -> client.getAccountId().equals(accountId) && client.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByType(final String clientType, final Page<Long> page) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(client -> Objects.equals(client.getClientType(), clientType) && client.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByDomain(final String domain, final Page<Long> page) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(client -> Objects.equals(client.getDomain(), domain) && client.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }
}
