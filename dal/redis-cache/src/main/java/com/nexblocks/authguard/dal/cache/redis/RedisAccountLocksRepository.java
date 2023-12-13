package com.nexblocks.authguard.dal.cache.redis;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.cache.AccountLocksRepository;
import com.nexblocks.authguard.dal.cache.redis.core.LettuceClientWrapper;
import com.nexblocks.authguard.dal.cache.redis.core.RedisRepository;
import com.nexblocks.authguard.dal.model.AccountLockDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RedisAccountLocksRepository implements AccountLocksRepository {
    private static final Logger LOG = LoggerFactory.getLogger(RedisAccountLocksRepository.class);

    private final RedisRepository<AccountLockDO> redisRepository;

    @Inject
    public RedisAccountLocksRepository(final LettuceClientWrapper clientWrapper) {
        this.redisRepository = new RedisRepository<>(clientWrapper, AccountLockDO.class);
    }

    @Override
    public CompletableFuture<Collection<AccountLockDO>> findByAccountId(final long accountId) {
        LOG.debug("Getting lock for account {}", accountId);

        return redisRepository.get(key(accountId))
                .thenApply(opt -> opt
                        .map(Collections::singleton)
                        .orElseGet(Collections::emptySet));
    }

    @Override
    public CompletableFuture<AccountLockDO> save(final AccountLockDO lock) {
        final Duration ttl = Duration.between(Instant.now(), lock.getExpiresAt());

        LOG.debug("Storing lock for account {}", lock.getAccountId());

        return redisRepository.save(key(lock.getAccountId()), lock, ttl.toSeconds());
    }

    @Override
    public CompletableFuture<Optional<AccountLockDO>> delete(final long accountId) {
        LOG.debug("Removing lock for account {}", accountId);

        return redisRepository.delete(key(accountId));
    }

    private String key(final long accountId) {
        return "lock:" + accountId;
    }
}
