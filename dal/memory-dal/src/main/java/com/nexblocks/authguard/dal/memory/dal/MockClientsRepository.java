package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.ClientDO;
import com.nexblocks.authguard.dal.persistence.ClientsRepository;

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
    public CompletableFuture<List<ClientDO>> getAllForAccount(final String accountId) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(app -> app.getAccountId().equals(accountId))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByType(String clientType) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(client -> Objects.equals(client.getClientType(), clientType))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByDomain(String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(client -> Objects.equals(client.getDomain(), domain))
                .collect(Collectors.toList()));
    }
}
