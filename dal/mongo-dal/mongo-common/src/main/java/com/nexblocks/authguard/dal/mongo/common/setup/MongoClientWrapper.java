package com.nexblocks.authguard.dal.mongo.common.setup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.nexblocks.authguard.config.ConfigContext;
import com.nexblocks.authguard.dal.mongo.config.ImmutableMongoConfiguration;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Singleton
public class MongoClientWrapper {
    private final MongoClient mongoClient;
    private final ImmutableMongoConfiguration config;

    @Inject
    public MongoClientWrapper(final @Named("mongo") ConfigContext config) {
        this(config.asConfigBean(ImmutableMongoConfiguration.class));
    }

    public MongoClientWrapper(final ImmutableMongoConfiguration config) {
        final CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                new Java8TimeCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(config.getConnectionString()))
                .codecRegistry(pojoCodecRegistry);

        final MongoClientSettings settings;

        if (config.getUsername() != null) {
            settings = settingsBuilder
                    .credential(MongoCredential.createCredential(config.getUsername(), config.getDatabase(),
                            config.getPassword().toCharArray()))
                    .build();
        } else {
            settings = settingsBuilder.build();
        }

        this.config = config;
        this.mongoClient = MongoClients.create(settings);
    }

    public MongoClient getClient() {
        return this.mongoClient;
    }

    public ImmutableMongoConfiguration getConfig() {
        return this.config;
    }
}
