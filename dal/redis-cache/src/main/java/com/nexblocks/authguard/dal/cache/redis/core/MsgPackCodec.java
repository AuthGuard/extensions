package com.nexblocks.authguard.dal.cache.redis.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;

public class MsgPackCodec<T> implements EntityCodec<T> {
    private final Class<T> entityType;
    private final ObjectMapper objectMapper;

    public MsgPackCodec(final Class<T> entityType) {
        this.entityType = entityType;
        this.objectMapper = new ObjectMapper(new MessagePackFactory());

        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public T deserialize(final byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, entityType);
        } catch (final IOException e) {
            throw new CodecException("Failed to deserialize to " + entityType.getSimpleName(), e);
        }
    }

    @Override
    public byte[] serialize(final T entity) {
        try {
            return objectMapper.writeValueAsBytes(entity);
        } catch (final JsonProcessingException e) {
            throw new CodecException("Failed to serialize " + entityType.getSimpleName(), e);
        }
    }
}
