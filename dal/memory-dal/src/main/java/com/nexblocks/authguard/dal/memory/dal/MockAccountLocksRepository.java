package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.cache.AccountLocksRepository;
import com.nexblocks.authguard.dal.model.AccountLockDO;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockAccountLocksRepository extends AbstractRepository<AccountLockDO>
        implements AccountLocksRepository {
    @Override
    public CompletableFuture<Collection<AccountLockDO>> findByAccountId(final String s) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .filter(lock -> s.equals(lock.getAccountId()))
                .collect(Collectors.toList()));
    }
}
