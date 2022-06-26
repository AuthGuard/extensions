package com.nexblocks.authguard.dal.cache.redis;

import com.nexblocks.authguard.dal.cache.redis.config.ImmutableRedisConfiguration;
import com.nexblocks.authguard.dal.cache.redis.core.LettuceClientWrapper;
import com.nexblocks.authguard.dal.model.SessionDO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import redis.embedded.RedisServer;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisSessionsRepositoryTest {
    private RedisServer redisServer;
    private RedisSessionsRepository redisSessionsRepository;

    @BeforeAll
    void setup() {
        redisServer = new RedisServer();

        redisServer.start();

        final ImmutableRedisConfiguration redisConfiguration = ImmutableRedisConfiguration.builder()
                .connectionString("redis://localhost")
                .build();

        redisSessionsRepository = new RedisSessionsRepository(new LettuceClientWrapper(redisConfiguration));
    }

    @AfterAll
    void destroy() {
        redisServer.stop();
    }

    @Test
    void saveAndGetById() {
        final SessionDO session = SessionDO.builder()
                .id("session-id")
                .sessionToken("session-token")
                .expiresAt(OffsetDateTime.now(Clock.systemUTC()).plusMinutes(5).withNano(0))
                .build();

        final SessionDO cached = redisSessionsRepository.save(session).join();

        assertThat(cached).isEqualTo(session);

        final Optional<SessionDO> retrieved = redisSessionsRepository.getByToken(session.getSessionToken())
                .join();

        assertThat(retrieved).isPresent()
                .get()
                .isEqualToIgnoringGivenFields(session, "expiresAt");
    }

}