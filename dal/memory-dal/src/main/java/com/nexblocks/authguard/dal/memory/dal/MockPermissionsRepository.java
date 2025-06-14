package com.nexblocks.authguard.dal.memory.dal;

import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.persistence.Page;
import com.nexblocks.authguard.dal.persistence.PermissionsRepository;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Optional;
import io.smallrye.mutiny.Uni;
import java.util.stream.Collectors;

@Singleton
public class MockPermissionsRepository extends AbstractRepository<PermissionDO>
        implements PermissionsRepository {
    @Override
    public Uni<Collection<PermissionDO>> getAll(final String domain, final Page<Long> page) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(permission -> permission.getDomain().equals(domain) && permission.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }

    @Override
    public Uni<Optional<PermissionDO>> search(final String group, final String name, final String domain) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(permission -> permission.getPermissionGroup().equals(group)
                        && permission.getName().equals(name) && permission.getDomain().equals(domain))
                .findFirst());
    }

    @Override
    public Uni<Collection<PermissionDO>> getAllForGroup(final String group, final String domain,
                                                                      final Page<Long> page) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(permission -> permission.getPermissionGroup().equals(group)
                        && permission.getDomain().equals(domain)
                        && permission.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }
}
