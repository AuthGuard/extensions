package com.nexblocks.authguard.dal.cache.redis;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.cache.AccountTokensRepository;
import com.nexblocks.authguard.dal.cache.redis.core.LettuceClientWrapper;
import com.nexblocks.authguard.dal.cache.redis.core.RedisRepository;
import com.nexblocks.authguard.dal.model.AccountTokenDO;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RedisAccountTokensRepository implements AccountTokensRepository {
    private static final Logger LOG = LoggerFactory.getLogger(RedisAccountTokensRepository.class);

    private final RedisRepository<AccountTokenDO> redisRepository;

    @Inject
    public RedisAccountTokensRepository(final LettuceClientWrapper clientWrapper) {
        this.redisRepository = new RedisRepository<>(clientWrapper, AccountTokenDO.class);
    }

    @Override
    public Uni<AccountTokenDO> save(final AccountTokenDO accountToken) {
        final Duration ttl = Duration.between(Instant.now(), accountToken.getExpiresAt());

        LOG.debug("Storing account token {}", accountToken.getToken());

        return redisRepository.save(accountToken.getToken(), accountToken);
    }

    @Override
    public CompletableFuture<Optional<AccountTokenDO>> getByToken(final String token) {
        LOG.debug("Getting account token {}", token);

        return redisRepository.get(token);
    }

    @Override
    public CompletableFuture<Optional<AccountTokenDO>> deleteToken(final String token) {
        return redisRepository.delete(token);
    }
}
