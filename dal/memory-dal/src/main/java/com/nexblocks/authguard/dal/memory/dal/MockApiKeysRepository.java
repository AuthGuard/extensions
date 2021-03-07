package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.ApiKeyDO;
import com.nexblocks.authguard.dal.persistence.ApiKeysRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockApiKeysRepository extends AbstractRepository<ApiKeyDO> implements ApiKeysRepository {
    @Override
    public CompletableFuture<Collection<ApiKeyDO>> getByAppId(final String id) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(key -> key.getAppId().equals(id))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Optional<ApiKeyDO>> getByKey(final String apiKey) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(key -> key.getKey().equals(apiKey))
                .findFirst());
    }
}
