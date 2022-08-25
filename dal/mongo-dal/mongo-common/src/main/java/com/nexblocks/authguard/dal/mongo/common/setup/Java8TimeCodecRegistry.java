package com.nexblocks.authguard.dal.mongo.common.setup;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.Instant;
import java.time.ZonedDateTime;

public class Java8TimeCodecRegistry implements CodecRegistry {
    private final ZonedDateTimeCodec zonedDateTimeCodec;
    private final OffsetDateTimeCodec offsetDateTimeCodec;

    public Java8TimeCodecRegistry() {
        zonedDateTimeCodec = new ZonedDateTimeCodec();
        offsetDateTimeCodec = new OffsetDateTimeCodec();
    }

    @Override
    public <T> Codec<T> get(final Class<T> clazz) {
        if (ZonedDateTime.class.isAssignableFrom(clazz)) {
            return (Codec<T>) zonedDateTimeCodec;
        }

        if (Instant.class.isAssignableFrom(clazz)) {
            return (Codec<T>) offsetDateTimeCodec;
        }

        return null;
    }

    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        return get(clazz);
    }
}
