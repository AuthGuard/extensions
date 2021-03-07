package com.nexblocks.authguard.dal.cache.redis.core;

import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringByteArrayCodec implements RedisCodec<String, byte[]> {

    private final StringCodec stringCodec;
    private final ByteArrayCodec byteArrayCodec;

    public StringByteArrayCodec() {
        stringCodec = new StringCodec(Charset.defaultCharset());
        byteArrayCodec = new ByteArrayCodec();
    }

    @Override
    public String decodeKey(final ByteBuffer byteBuffer) {
        return stringCodec.decodeKey(byteBuffer);
    }

    @Override
    public byte[] decodeValue(final ByteBuffer byteBuffer) {
        return byteArrayCodec.decodeValue(byteBuffer);
    }

    @Override
    public ByteBuffer encodeKey(final String key) {
        return stringCodec.encodeKey(key);
    }

    @Override
    public ByteBuffer encodeValue(final byte[] bytes) {
        return byteArrayCodec.encodeValue(bytes);
    }
}
