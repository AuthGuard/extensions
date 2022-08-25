package com.nexblocks.authguard.dal.cache.redis;

import com.nexblocks.authguard.dal.cache.redis.config.ImmutableRedisConfiguration;
import com.nexblocks.authguard.dal.cache.redis.core.LettuceClientWrapper;
import com.nexblocks.authguard.dal.model.AccountLockDO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import redis.embedded.RedisServer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisAccountLocksRepositoryTest {
    private RedisServer redisServer;
    private RedisAccountLocksRepository redisAccountLocksRepository;

    @BeforeAll
    void setup() {
        redisServer = new RedisServer();

        redisServer.start();

        final ImmutableRedisConfiguration redisConfiguration = ImmutableRedisConfiguration.builder()
                .connectionString("redis://localhost")
                .build();

        redisAccountLocksRepository = new RedisAccountLocksRepository(new LettuceClientWrapper(redisConfiguration));
    }

    @AfterAll
    void destroy() {
        redisServer.stop();
    }

    @Test
    void saveAndGetByAccountId() {
        final AccountLockDO lock = AccountLockDO.builder()
                .accountId("account")
                .expiresAt(Instant.now(Clock.systemUTC()).plus(Duration.ofMinutes(5)))
                .build();

        final AccountLockDO cached = redisAccountLocksRepository.save(lock).join();

        assertThat(cached).isEqualTo(lock);

        final Collection<AccountLockDO> retrieved = redisAccountLocksRepository.findByAccountId(lock.getAccountId())
                .join();

        assertThat(retrieved).contains(lock);
    }
}