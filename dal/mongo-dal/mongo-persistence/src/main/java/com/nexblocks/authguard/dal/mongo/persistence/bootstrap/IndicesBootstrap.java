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

        database.getCollection(permissionsCollection)
                .createIndex(permissionsIndex, new IndexOptions().unique(true).name("permissions.groupAndName.index"))
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        // ---------------
        LOG.info("Bootstrapping roles index");
        final String rolesCollection = config.getCollections()
                .getOrDefault("roles", Defaults.Collections.ROLES);

        final Bson rolesIndex = Indexes.ascending("name");

        database.getCollection(rolesCollection)
                .createIndex(rolesIndex, new IndexOptions().unique(true).name("roles.name.index"))
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        // ---------------
        LOG.info("Bootstrapping accounts index");
        final String accountsCollection = config.getCollections()
                .getOrDefault("accounts", Defaults.Collections.ACCOUNTS);

        final Bson accountEmailIndex = Indexes.ascending("email.email");
        final Bson accountBackupEmailIndex = Indexes.ascending("backupEmail.email");
        final Bson accountPhoneNumberIndex = Indexes.ascending("phoneNumber.number");

        database.getCollection(accountsCollection)
                .createIndex(accountEmailIndex, new IndexOptions().unique(true).name("accounts.email.index"))
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        database.getCollection(accountsCollection)
                .createIndex(accountBackupEmailIndex, new IndexOptions().unique(true).name("accounts.backupEmail.index"))
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        database.getCollection(accountsCollection)
                .createIndex(accountPhoneNumberIndex, new IndexOptions().unique(true).name("accounts.phoneNumber.index"))
                .subscribe(new LogSubscriber<>("Created index {}", LOG, this::handleExceptions));

        // ---------------
        LOG.info("Bootstrapping credentials index");
        final String credentialsCollection = config.getCollections()
                .getOrDefault("credentials", Defaults.Collections.CREDENTIALS);

        final Bson identifiersIndex = Indexes.ascending("identifiers.identifier");

        database.getCollection(credentialsCollection)
                .createIndex(identifiersIndex, new IndexOptions().unique(true).name("credentials.identifier.index"))
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
