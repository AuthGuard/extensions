package com.nexblocks.authguard.dal.memory.dal;

import com.nexblocks.authguard.dal.model.RoleDO;
import com.nexblocks.authguard.dal.persistence.RolesRepository;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockRolesRepository extends AbstractRepository<RoleDO> implements RolesRepository {
    @Override
    public CompletableFuture<Collection<RoleDO>> getAll(final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(role -> role.getDomain().equals(domain))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Optional<RoleDO>> getByName(final String name, final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(role -> role.getName().equals(name) && role.getDomain().equals(domain))
                .findFirst());
    }

    @Override
    public CompletableFuture<Collection<RoleDO>> getMultiple(final Collection<String> collection, final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(role -> collection.contains(role.getName()) && role.getDomain().equals(domain))
                .collect(Collectors.toList()));
    }
}
