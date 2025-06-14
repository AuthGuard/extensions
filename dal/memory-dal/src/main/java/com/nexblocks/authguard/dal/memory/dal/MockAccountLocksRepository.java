package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.cache.AccountLocksRepository;
import com.nexblocks.authguard.dal.model.AccountLockDO;

import java.util.Collection;
import io.smallrye.mutiny.Uni;
import java.util.stream.Collectors;

@Singleton
public class MockAccountLocksRepository extends AbstractRepository<AccountLockDO>
        implements AccountLocksRepository {
    @Override
    public Uni<Collection<AccountLockDO>> findByAccountId(final long s) {
        return Uni.createFrom().item(getRepo().values()
                .stream()
                .filter(lock -> s == lock.getAccountId())
                .collect(Collectors.toList()));
    }
}
