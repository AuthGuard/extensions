package com.nexblocks.authguard.dal.cache.redis.core;

import com.nexblocks.authguard.dal.model.AbstractDO;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.smallrye.mutiny.Uni;

import java.util.Optional;
import io.smallrye.mutiny.Uni;

public class RedisRepository<T extends AbstractDO> {
    private final EntityCodec<T> entityCodec;
    private final LettuceClientWrapper clientWrapper;

    public RedisRepository(final LettuceClientWrapper clientWrapper, final Class<T> entityType) {
        this.clientWrapper = clientWrapper;
        this.entityCodec = new MsgPackCodec<>(entityType);
    }

    public Uni<Optional<T>> get(final String key) {
        final RedisAsyncCommands<String, byte[]> commands = clientWrapper.getConnection().async();

        return Uni.createFrom().completionStage(commands.get(key).toCompletableFuture())
                .map(value -> {
                    if (value == null) {
                        return Optional.empty();
                    } else {
                        return Optional.of(entityCodec.deserialize(value));
                    }
                });
    }

    public Uni<Optional<T>> get(final long key) {
        return get(String.valueOf(key));
    }

    public Uni<T> save(final String key, final T value, final long ttl) {
        final byte[] serialized = entityCodec.serialize(value);
        final RedisAsyncCommands<String, byte[]> commands = clientWrapper.getConnection().async();

        return Uni.createFrom().completionStage(
                commands.set(key, serialized)
                        .thenCompose(ignored -> commands.expire(key, ttl))
                        .toCompletableFuture()
                ).map(stored -> value);
    }

    public Uni<T> save(final String key, final T value) {
        final byte[] serialized = entityCodec.serialize(value);
        final RedisAsyncCommands<String, byte[]> commands = clientWrapper.getConnection().async();

        return Uni.createFrom().completionStage(commands.set(key, serialized).toCompletableFuture())
                .map(stored -> value);
    }

    public Uni<T> save(final long key, final T value) {
        return save(String.valueOf(key), value);
    }

    public Uni<Optional<T>> delete(final String key) {
        final RedisAsyncCommands<String, byte[]> commands = clientWrapper.getConnection().async();

        return get(key)
                .flatMap(value -> {
                    if (value.isPresent()) {
                        return Uni.createFrom().completionStage(commands.del(key).toCompletableFuture())
                                .map(ignored -> value);
                    } else {
                        return Uni.createFrom().item(Optional.empty());
                    }
                });
    }
}
