package com.nexblocks.authguard.dal.cache.redis;

import com.nexblocks.authguard.dal.cache.redis.config.ImmutableRedisConfiguration;
import com.nexblocks.authguard.dal.cache.redis.core.LettuceClientWrapper;
import com.nexblocks.authguard.dal.model.AccountTokenDO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import redis.embedded.RedisServer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisAccountTokensRepositoryTest {
    private RedisServer redisServer;
    private RedisAccountTokensRepository redisAccountTokensRepository;

    @BeforeAll
    void setup() {
        redisServer = new RedisServer();

        redisServer.start();

        final ImmutableRedisConfiguration redisConfiguration = ImmutableRedisConfiguration.builder()
                .connectionString("redis://localhost")
                .build();

        redisAccountTokensRepository = new RedisAccountTokensRepository(new LettuceClientWrapper(redisConfiguration));
    }

    @AfterAll
    void destroy() {
        redisServer.stop();
    }

    @Test
    void saveAndGetByToken() {
        final AccountTokenDO accountToken = AccountTokenDO.builder()
                .associatedAccountId("account")
                .token("token")
                .expiresAt(Instant.now(Clock.systemUTC()).plus(Duration.ofMinutes(5)))
                .build();

        final AccountTokenDO cached = redisAccountTokensRepository.save(accountToken).join();

        assertThat(cached).isEqualTo(accountToken);

        final Optional<AccountTokenDO> retrieved = redisAccountTokensRepository.getByToken(accountToken.getToken())
                .join();

        assertThat(retrieved).isPresent()
                .get()
                .isEqualToIgnoringGivenFields(accountToken, "expiresAt");
    }
}