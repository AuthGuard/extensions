package com.nexblocks.authguard.dal.cache.redis.core;

import com.nexblocks.authguard.dal.model.AbstractDO;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RedisRepository<T extends AbstractDO> {
    private final EntityCodec<T> entityCodec;
    private final LettuceClientWrapper clientWrapper;

    public RedisRepository(final LettuceClientWrapper clientWrapper, final Class<T> entityType) {
        this.clientWrapper = clientWrapper;
        this.entityCodec = new MsgPackCodec<>(entityType);
    }

    public CompletableFuture<Optional<T>> get(final String key) {
        final RedisAsyncCommands<String, byte[]> commands = clientWrapper.getConnection().async();

        return commands.get(key)
                .toCompletableFuture()
                .thenApply(value -> {
                    if (value == null) {
                        return Optional.empty();
                    } else {
                        return Optional.of(entityCodec.deserialize(value));
                    }
                });
    }

    public CompletableFuture<T> save(final String key, final T value, final long ttl) {
        final byte[] serialized = entityCodec.serialize(value);
        final RedisAsyncCommands<String, byte[]> commands = clientWrapper.getConnection().async();

        return commands.set(key, serialized)
                .thenCompose(ignored -> commands.expire(key, ttl))
                .toCompletableFuture()
                .thenApply(stored -> value);
    }

    public CompletableFuture<T> save(final String key, final T value) {
        final byte[] serialized = entityCodec.serialize(value);
        final RedisAsyncCommands<String, byte[]> commands = clientWrapper.getConnection().async();

        return commands.set(key, serialized)
                .toCompletableFuture()
                .thenApply(stored -> value);
    }

    public CompletableFuture<Optional<T>> delete(final String key) {
        final RedisAsyncCommands<String, byte[]> commands = clientWrapper.getConnection().async();

        return get(key)
                .toCompletableFuture()
                .thenCompose(value -> {
                    if (value.isPresent()) {
                        return commands.del(key)
                                .toCompletableFuture()
                                .thenApply(ignored -> value);
                    } else {
                        return CompletableFuture.completedFuture(Optional.empty());
                    }
                });
    }
}
