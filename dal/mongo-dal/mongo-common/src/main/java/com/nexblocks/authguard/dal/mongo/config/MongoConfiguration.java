package com.nexblocks.authguard.dal.mongo.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@Value.Style(
        validationMethod = Value.Style.ValidationMethod.NONE,
        jdkOnly = true
)
@JsonSerialize(as = ImmutableMongoConfiguration.class)
@JsonDeserialize(as = ImmutableMongoConfiguration.class)
public interface MongoConfiguration {
    String getDatabase();
    String getUsername();
    String getPassword();
    String getConnectionString();
    Map<String, String> getCollections();
}
