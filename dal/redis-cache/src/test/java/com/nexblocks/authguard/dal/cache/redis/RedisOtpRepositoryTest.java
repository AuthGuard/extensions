package com.nexblocks.authguard.dal.cache.redis;

import com.nexblocks.authguard.dal.cache.redis.config.ImmutableRedisConfiguration;
import com.nexblocks.authguard.dal.cache.redis.core.LettuceClientWrapper;
import com.nexblocks.authguard.dal.model.OneTimePasswordDO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import redis.embedded.RedisServer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisOtpRepositoryTest {
//    private RedisServer redisServer;
//    private RedisOtpRepository redisOtpRepository;
//
//    @BeforeAll
//    void setup() {
//        redisServer = new RedisServer();
//
//        redisServer.start();
//
//        final ImmutableRedisConfiguration redisConfiguration = ImmutableRedisConfiguration.builder()
//                .connectionString("redis://localhost")
//                .build();
//
//        redisOtpRepository = new RedisOtpRepository(new LettuceClientWrapper(redisConfiguration));
//    }
//
//    @AfterAll
//    void destroy() {
//        redisServer.stop();
//    }
//
//    @Test
//    void saveAndGetById() {
//        final OneTimePasswordDO otp = OneTimePasswordDO.builder()
//                .id(1)
//                .password("password")
//                .accountId(101)
//                .expiresAt(Instant.now(Clock.systemUTC()).plus(Duration.ofMinutes(5)))
//                .build();
//
//        final OneTimePasswordDO cached = redisOtpRepository.save(otp).join();
//
//        assertThat(cached).isEqualTo(otp);
//
//        final Optional<OneTimePasswordDO> retrieved = redisOtpRepository.getById(otp.getId()).join();
//
//        assertThat(retrieved).isPresent()
//                .get()
//                .isEqualToIgnoringGivenFields(otp, "expiresAt");
//    }
}