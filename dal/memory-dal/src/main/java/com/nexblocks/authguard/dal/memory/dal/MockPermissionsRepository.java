package com.nexblocks.authguard.dal.memory.dal;

import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.persistence.Page;
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
    public CompletableFuture<Collection<PermissionDO>> getAll(final String domain, final Page page) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(permission -> permission.getDomain().equals(domain) && permission.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Optional<PermissionDO>> search(final String group, final String name, final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(permission -> permission.getGroup().equals(group)
                        && permission.getName().equals(name) && permission.getDomain().equals(domain))
                .findFirst());
    }

    @Override
    public CompletableFuture<Collection<PermissionDO>> getAllForGroup(final String group, final String domain,
                                                                      final Page page) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(permission -> permission.getGroup().equals(group)
                        && permission.getDomain().equals(domain)
                        && permission.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }
}
