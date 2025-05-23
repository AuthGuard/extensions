package com.nexblocks.authguard.dal.cache.redis;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.cache.OtpRepository;
import com.nexblocks.authguard.dal.cache.redis.core.LettuceClientWrapper;
import com.nexblocks.authguard.dal.cache.redis.core.RedisRepository;
import com.nexblocks.authguard.dal.model.OneTimePasswordDO;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RedisOtpRepository implements OtpRepository {
    private static final Logger LOG = LoggerFactory.getLogger(RedisOtpRepository.class);

    private final RedisRepository<OneTimePasswordDO> redisRepository;

    @Inject
    public RedisOtpRepository(final LettuceClientWrapper clientWrapper) {
        this.redisRepository = new RedisRepository<>(clientWrapper, OneTimePasswordDO.class);
    }

    @Override
    public Uni<OneTimePasswordDO> save(final OneTimePasswordDO otp) {
        final Duration ttl = Duration.between(Instant.now(), otp.getExpiresAt());

        LOG.debug("Storing OTP {}", otp.getId());

        return redisRepository.save(otp.getId(), otp);
    }

    @Override
    public Uni<Optional<OneTimePasswordDO>> getById(final long id) {
        LOG.debug("Getting OTP {}", id);

        return Uni.createFrom().completionStage(redisRepository.get(id));
    }
}
