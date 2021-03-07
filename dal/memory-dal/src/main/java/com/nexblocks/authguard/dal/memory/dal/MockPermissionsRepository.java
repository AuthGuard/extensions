package com.nexblocks.authguard.dal.memory.dal;

import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.persistence.PermissionsRepository;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockPermissionsRepository extends AbstractRepository<PermissionDO>
        implements PermissionsRepository {
    @Override
    public CompletableFuture<Optional<PermissionDO>> search(final String group, final String name) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(permission -> permission.getGroup().equals(group) && permission.getName().equals(name))
                .findFirst());
    }

    @Override
    public CompletableFuture<Collection<PermissionDO>> getAllForGroup(final String group) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(permission -> permission.getGroup().equals(group))
                .collect(Collectors.toList()));
    }
}
