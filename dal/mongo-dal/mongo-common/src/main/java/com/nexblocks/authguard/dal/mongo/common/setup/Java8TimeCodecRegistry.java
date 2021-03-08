package com.nexblocks.authguard.dal.mongo.common.setup;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.ZonedDateTime;

public class Java8TimeCodecRegistry implements CodecRegistry {
    private final ZonedDateTimeCodec zonedDateTimeCodec;

    public Java8TimeCodecRegistry() {
        zonedDateTimeCodec = new ZonedDateTimeCodec();
    }

    @Override
    public <T> Codec<T> get(final Class<T> clazz) {
        if (ZonedDateTime.class.isAssignableFrom(clazz)) {
            return (Codec<T>) zonedDateTimeCodec;
        }

        return null;
    }

    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        return get(clazz);
    }
}
