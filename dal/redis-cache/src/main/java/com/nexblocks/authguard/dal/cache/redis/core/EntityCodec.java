package com.nexblocks.authguard.dal.cache.redis.core;

public interface EntityCodec<T> {
    T deserialize(byte[] bytes);
    byte[] serialize(T entity);
}
