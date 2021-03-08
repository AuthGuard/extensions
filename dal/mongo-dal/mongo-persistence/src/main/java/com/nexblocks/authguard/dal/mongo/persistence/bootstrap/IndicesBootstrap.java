package com.nexblocks.authguard.dal.mongo.persistence.bootstrap;

import com.google.inject.Inject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.nexblocks.authguard.bootstrap.BootstrapStep;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.common.subscribers.LogSubscriber;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.mongo.config.ImmutableMongoConfiguration;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndicesBootstrap implements BootstrapStep {
    private static final Logger LOG = LoggerFactory.getLogger(IndicesBootstrap.class);

    private final MongoDatabase database;
    private final ImmutableMongoConfiguration config;

    @Inject
    public IndicesBootstrap(final MongoClientWrapper clientWrapper) {
        this.database = clientWrapper.getClient()
                .getDatabase(clientWrapper.getConfig().getDatabase());
        this.config = clientWrapper.getConfig();
    }

    @Override
    public void run() {
        LOG.info("Bootstrapping permissions indices");
        final String permissionsCollection = config.getCollections()
                .getOrDefault("permissions", Defaults.Collections.PERMISSIONS);

        final Bson permissionsIndex = Indexes.compoundIndex(
                Indexes.text("group"),
                Indexes.text("name")
        );

        final IndexOptions permissionsIndexOptions = new IndexOptions()
                .unique(true);

        database.getCollection(permissionsCollection).createIndex(permissionsIndex, permissionsIndexOptions)
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        // ---------------
        LOG.info("Bootstrapping roles index");
        final String rolesCollection = config.getCollections()
                .getOrDefault("roles", Defaults.Collections.ROLES);

        final Bson rolesIndex = Indexes.text("name");

        final IndexOptions rolesIndexOptions = new IndexOptions()
                .unique(true);

        database.getCollection(rolesCollection).createIndex(rolesIndex, rolesIndexOptions)
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));
    }

    private void handleExceptions(final Throwable e) {
        if (e instanceof DuplicateKeyException) {
            LOG.info("Index already exists");
        } else {
            LOG.error("An error occurred", e);
        }
    }
}
