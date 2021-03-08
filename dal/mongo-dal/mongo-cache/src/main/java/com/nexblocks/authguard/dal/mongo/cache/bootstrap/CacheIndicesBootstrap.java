package com.nexblocks.authguard.dal.mongo.cache.bootstrap;

import com.google.inject.Inject;
import com.mongodb.DuplicateKeyException;
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

public class CacheIndicesBootstrap implements BootstrapStep {
    private static final Logger LOG = LoggerFactory.getLogger(CacheIndicesBootstrap.class);

    private final MongoDatabase database;
    private final ImmutableMongoConfiguration config;

    @Inject
    public CacheIndicesBootstrap(final MongoClientWrapper clientWrapper) {
        this.database = clientWrapper.getClient()
                .getDatabase(clientWrapper.getConfig().getDatabase());
        this.config = clientWrapper.getConfig();
    }

    @Override
    public void run() {
        LOG.info("Bootstrapping account locks indices");
        final String accountLocksCollection = config.getCollections()
                .getOrDefault("account_locks", Defaults.Collections.PERMISSIONS);

        final Bson accountLocksAccountIdIndex = Indexes.text("accountId");

        database.getCollection(accountLocksCollection).createIndex(accountLocksAccountIdIndex)
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        // ---------------
        LOG.info("Bootstrapping account tokens indices");
        final String accountTokensCollection = config.getCollections()
                .getOrDefault("account_tokens", Defaults.Collections.PERMISSIONS);

        final Bson accountTokensAccountIdIndex = Indexes.text("accountId");
        final Bson accountTokensTokenIndex = Indexes.text("token");

        database.getCollection(accountTokensCollection).createIndex(accountTokensAccountIdIndex)
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        database.getCollection(accountTokensCollection).createIndex(accountTokensTokenIndex)
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        // ----------------
        LOG.info("Bootstrapping sessions indices");
        final String sessionsCollection = config.getCollections()
                .getOrDefault("account_tokens", Defaults.Collections.PERMISSIONS);

        final Bson sessionsTokenIndex = Indexes.text("sessionToken");

        database.getCollection(sessionsCollection).createIndex(sessionsTokenIndex)
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
