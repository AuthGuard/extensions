package com.nexblocks.authguard.dal.cache.redis.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nexblocks.authguard.config.ConfigContext;
import com.nexblocks.authguard.dal.cache.redis.config.ImmutableRedisConfiguration;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

@Singleton
public class LettuceClientWrapper {
    private final RedisClient client;
    private final ImmutableRedisConfiguration config;
    private StatefulRedisConnection<String, byte[]> connection;

    @Inject
    public LettuceClientWrapper(final @Named("redis") ConfigContext config) {
        this(config.asConfigBean(ImmutableRedisConfiguration.class));
    }

    public LettuceClientWrapper(final ImmutableRedisConfiguration redisConfiguration) {
        this.config = redisConfiguration;
        this.client = RedisClient.create(this.config.getConnectionString());
    }

    public RedisClient getClient() {
        return client;
    }

    public StatefulRedisConnection<String, byte[]> getConnection() {
        if (connection == null) {
            connection = client.connect(new StringByteArrayCodec());
        }

        return connection;
    }
}
