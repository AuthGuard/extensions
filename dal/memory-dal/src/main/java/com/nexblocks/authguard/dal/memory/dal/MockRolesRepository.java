package com.nexblocks.authguard.dal.memory.dal;

import com.nexblocks.authguard.dal.model.RoleDO;
import com.nexblocks.authguard.dal.persistence.Page;
import com.nexblocks.authguard.dal.persistence.RolesRepository;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Optional;
import io.smallrye.mutiny.Uni;
import java.util.stream.Collectors;

@Singleton
public class MockRolesRepository extends AbstractRepository<RoleDO> implements RolesRepository {
    @Override
    public Uni<Collection<RoleDO>> getAll(final String domain, final Page<Long> page) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(role -> role.getDomain().equals(domain) && role.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }

    @Override
    public Uni<Optional<RoleDO>> getByName(final String name, final String domain) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(role -> role.getName().equals(name) && role.getDomain().equals(domain))
                .findFirst());
    }

    @Override
    public Uni<Collection<RoleDO>> getMultiple(final Collection<String> collection, final String domain) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(role -> collection.contains(role.getName()) && role.getDomain().equals(domain))
                .collect(Collectors.toList()));
    }
}
