package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.ApiKeyDO;
import com.nexblocks.authguard.dal.persistence.ApiKeysRepository;
import io.smallrye.mutiny.Uni;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockApiKeysRepository extends AbstractRepository<ApiKeyDO> implements ApiKeysRepository {
    @Override
    public Uni<Collection<ApiKeyDO>> getByAppId(final long id) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(key -> key.getAppId() == id)
                .collect(Collectors.toList()));
    }

    @Override
    public Uni<Optional<ApiKeyDO>> getByKey(final String apiKey) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(key -> key.getKey().equals(apiKey))
                .findFirst());
    }
}
