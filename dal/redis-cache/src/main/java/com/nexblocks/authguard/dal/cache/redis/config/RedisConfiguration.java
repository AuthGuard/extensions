package com.nexblocks.authguard.dal.cache.redis.config;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
        validationMethod = Value.Style.ValidationMethod.NONE
)
public interface RedisConfiguration {
    String getConnectionString();
}
