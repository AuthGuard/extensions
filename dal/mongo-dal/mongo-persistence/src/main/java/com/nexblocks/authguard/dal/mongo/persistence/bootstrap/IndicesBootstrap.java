package com.nexblocks.authguard.dal.mongo.persistence.bootstrap;

import com.google.inject.Inject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.MongoDatabase;
import com.nexblocks.authguard.bootstrap.BootstrapStep;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.common.subscribers.WaitForCompletion;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.mongo.config.ImmutableMongoConfiguration;
import org.bson.BsonType;
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

    // TODO use WaitForCompletion.wait() after moving back to the reactive driver
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
                .createIndex(permissionsIndex, new IndexOptions().unique(true).name("permissions.groupAndName.index"));

        // ---------------
        LOG.info("Bootstrapping roles index");
        final String rolesCollection = config.getCollections()
                .getOrDefault("roles", Defaults.Collections.ROLES);

        final Bson rolesIndex = Indexes.ascending("name");

        database.getCollection(rolesCollection)
                .createIndex(rolesIndex, new IndexOptions().unique(true).name("roles.name.index"));

        // ---------------
        LOG.info("Bootstrapping accounts index");
        final String accountsCollection = config.getCollections()
                .getOrDefault("accounts", Defaults.Collections.ACCOUNTS);

        final Bson accountEmailIndex = Indexes.ascending("email.email");
        final Bson accountBackupEmailIndex = Indexes.ascending("backupEmail.email");
        final Bson accountPhoneNumberIndex = Indexes.ascending("phoneNumber.number");

        database.getCollection(accountsCollection)
                .createIndex(accountEmailIndex, new IndexOptions()
                        .unique(true)
                        .partialFilterExpression(Filters.type("email.email", BsonType.STRING))
                        .name("accounts.email.index"));

        LOG.info("Created account email index");

        database.getCollection(accountsCollection)
                .createIndex(accountBackupEmailIndex, new IndexOptions()
                        .unique(true)
                        .partialFilterExpression(Filters.type("backupEmail.email", BsonType.STRING))
                        .name("accounts.backupEmail.index"));

        LOG.info("Created account backup email index");

        database.getCollection(accountsCollection)
                .createIndex(accountPhoneNumberIndex, new IndexOptions()
                        .unique(true)
                        .partialFilterExpression(Filters.type("phoneNumber.number", BsonType.STRING))
                        .name("accounts.phoneNumber.index"));

        LOG.info("Created phone number index");

        // ---------------
        LOG.info("Bootstrapping credentials index");
        final String credentialsCollection = config.getCollections()
                .getOrDefault("credentials", Defaults.Collections.CREDENTIALS);

        final Bson identifiersIndex = Indexes.ascending("identifiers.identifier");

        database.getCollection(credentialsCollection)
                .createIndex(identifiersIndex, new IndexOptions().unique(true).name("credentials.identifier.index"));

        LOG.info("Created credentials identifier index");
    }

    private void handleExceptions(final Throwable e) {
        if (e instanceof DuplicateKeyException) {
            LOG.info("Index already exists");
        } else {
            LOG.error("An error occurred", e);
        }
    }
}
