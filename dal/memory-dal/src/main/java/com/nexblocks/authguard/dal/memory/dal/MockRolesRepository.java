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
    public CompletableFuture<Optional<RoleDO>> getByName(final String name) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(role -> role.getName().equals(name))
                .findFirst());
    }

    @Override
    public CompletableFuture<Collection<RoleDO>> getMultiple(final Collection<String> collection) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(role -> collection.contains(role.getName()))
                .collect(Collectors.toList()));
    }
}
