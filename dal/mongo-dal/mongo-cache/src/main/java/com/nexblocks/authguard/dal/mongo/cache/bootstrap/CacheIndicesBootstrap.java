package com.nexblocks.authguard.dal.mongo.cache.bootstrap;

import com.google.inject.Inject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.MongoDatabase;
import com.nexblocks.authguard.bootstrap.BootstrapStep;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.common.subscribers.WaitForCompletion;
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
                .getOrDefault("account_locks", Defaults.Collections.ACCOUNT_LOCKS);

        final Bson accountLocksAccountIdIndex = Indexes.ascending("accountId");

        database.getCollection(accountLocksCollection)
                .createIndex(accountLocksAccountIdIndex, new IndexOptions().name("account_locks.accountId.index"));

        LOG.info("Created account locks index");

        // ---------------
        LOG.info("Bootstrapping account tokens indices");
        final String accountTokensCollection = config.getCollections()
                .getOrDefault("account_tokens", Defaults.Collections.ACCOUNT_TOKENS);

        final Bson accountTokensAccountIdIndex = Indexes.ascending("accountId");
        final Bson accountTokensTokenIndex = Indexes.ascending("token");

        database.getCollection(accountTokensCollection)
                .createIndex(accountTokensAccountIdIndex, new IndexOptions().name("account_tokens.accountId.index"));

        LOG.info("Created account token account ID index");

        database.getCollection(accountTokensCollection)
                .createIndex(accountTokensTokenIndex, new IndexOptions().name("account_tokens.token.index"));

        LOG.info("Created account token index");

        // ----------------
        LOG.info("Bootstrapping sessions indices");
        final String sessionsCollection = config.getCollections()
                .getOrDefault("sessions", Defaults.Collections.SESSIONS);

        final Bson sessionsTokenIndex = Indexes.ascending("sessionToken");

        database.getCollection(sessionsCollection)
                .createIndex(sessionsTokenIndex, new IndexOptions().name("sessions.sessionToken.index"));

        LOG.info("Created session token index");
    }

    private void handleExceptions(final Throwable e) {
        if (e instanceof DuplicateKeyException) {
            LOG.info("Index already exists");
        } else {
            LOG.error("An error occurred", e);
        }
    }
}
