package com.nexblocks.authguard.dal.mongo.common.setup;

import com.mongodb.MongoClientSettings;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Java8TimeCodecRegistryTest {

    @Test
    void getCodec() {
        final CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                new Java8TimeCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final Codec<ZonedDateTime> actual = pojoCodecRegistry.get(ZonedDateTime.class);

        assertEquals(ZonedDateTimeCodec.class, actual.getClass());
    }
}