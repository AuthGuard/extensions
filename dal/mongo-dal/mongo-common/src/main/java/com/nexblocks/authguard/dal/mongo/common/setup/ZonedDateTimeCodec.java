package com.nexblocks.authguard.dal.mongo.common.setup;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeCodec implements Codec<ZonedDateTime> {
    @Override
    public ZonedDateTime decode(final BsonReader bsonReader, final DecoderContext decoderContext) {
        bsonReader.readStartDocument();
        final String zoneId = bsonReader.readString("tz");
        final long timestamp = bsonReader.readInt64("ts");
        bsonReader.readEndDocument();

        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of(zoneId));
    }

    @Override
    public void encode(final BsonWriter bsonWriter, final ZonedDateTime zonedDateTime, final EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();
        bsonWriter.writeString("tz", zonedDateTime.getZone().getId());
        bsonWriter.writeInt64("ts", zonedDateTime.toInstant().toEpochMilli());
        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<ZonedDateTime> getEncoderClass() {
        return ZonedDateTime.class;
    }
}
