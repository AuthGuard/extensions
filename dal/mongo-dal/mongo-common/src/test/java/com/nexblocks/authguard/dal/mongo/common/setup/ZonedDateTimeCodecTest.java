package com.nexblocks.authguard.dal.mongo.common.setup;

import org.bson.*;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZonedDateTimeCodecTest {

    @Test
    void encodeAndDecode() {
        final ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        final ZonedDateTimeCodec codec = new ZonedDateTimeCodec();
        final BsonDocument bsonDocument = new BsonDocument();

        // encode the value
        final BsonWriter bsonWriter = new BsonDocumentWriter(bsonDocument);
        codec.encode(bsonWriter, now, EncoderContext.builder().build());

        // read it back
        final BsonReader bsonReader = new BsonDocumentReader(bsonDocument);
        final ZonedDateTime actual = codec.decode(bsonReader, DecoderContext.builder().build());

        // and compare
        assertEquals(now, actual);
    }

}