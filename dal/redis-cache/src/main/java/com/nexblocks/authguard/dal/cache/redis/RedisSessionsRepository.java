package com.nexblocks.authguard.dal.cache.redis;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.cache.SessionsRepository;
import com.nexblocks.authguard.dal.cache.redis.core.LettuceClientWrapper;
import com.nexblocks.authguard.dal.cache.redis.core.RedisRepository;
import com.nexblocks.authguard.dal.model.SessionDO;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RedisSessionsRepository implements SessionsRepository {
    private static final Logger LOG = LoggerFactory.getLogger(RedisSessionsRepository.class);

    private final RedisRepository<SessionDO> redisRepository;

    @Inject
    public RedisSessionsRepository(final LettuceClientWrapper clientWrapper) {
        this.redisRepository = new RedisRepository<>(clientWrapper, SessionDO.class);
    }

    @Override
    public Uni<SessionDO> save(final SessionDO session) {
        final Duration ttl = Duration.between(Instant.now(), session.getExpiresAt());

        LOG.debug("Storing session {}", session.getSessionToken());

        return redisRepository.save(session.getSessionToken(), session);
    }

    @Override
    public Uni<Optional<SessionDO>> getById(final long s) {
        throw new UnsupportedOperationException("Sessions cannot be retrieved by ID");
    }

    @Override
    public CompletableFuture<Optional<SessionDO>> getByToken(final String sessionToken) {
        LOG.debug("Getting session {}", sessionToken);

        return redisRepository.get(sessionToken);
    }

    @Override
    public CompletableFuture<Optional<SessionDO>> deleteByToken(final String sessionToken) {
        return redisRepository.delete(sessionToken);
    }

    @Override
    public CompletableFuture<List<SessionDO>> findByAccountId(final long accountId, final String domain) {
        throw new UnsupportedOperationException("Retrieving sessions by account ID isn't " +
                "currently supported by Redis cache implementation");
    }
}
