package com.nexblocks.authguard.dal.mongo.persistence.bootstrap;

import com.google.inject.Inject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.nexblocks.authguard.bootstrap.BootstrapStep;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
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
                Indexes.ascending("domain"),
                Indexes.ascending("group"),
                Indexes.ascending("name")
        );

        final IndexOptions permissionsIndexOptions = new IndexOptions()
                .unique(true)
                .name("permissions.groupAndName.index");

        createIndex(permissionsCollection, permissionsIndex, permissionsIndexOptions);

        // ---------------
        LOG.info("Bootstrapping roles index");
        final String rolesCollection = config.getCollections()
                .getOrDefault("roles", Defaults.Collections.ROLES);

        final Bson rolesIndex = Indexes.compoundIndex(
                Indexes.ascending("domain"),
                Indexes.ascending("name")
        );
        final IndexOptions rolesIndexOptions = new IndexOptions()
                .unique(true)
                .name("roles.name.index");

        createIndex(rolesCollection, rolesIndex, rolesIndexOptions);

        // ---------------
        LOG.info("Bootstrapping accounts index");
        final String accountsCollection = config.getCollections()
                .getOrDefault("accounts", Defaults.Collections.ACCOUNTS);

        final Bson emailIndex = Indexes.compoundIndex(
                Indexes.ascending("domain"),
                Indexes.ascending("email.email")
        );
        final Bson backupEmailIndex = Indexes.compoundIndex(
                Indexes.ascending("domain"),
                Indexes.ascending("backupEmail.email")
        );
        final Bson phoneNumberIndex = Indexes.compoundIndex(
                Indexes.ascending("domain"),
                Indexes.ascending("phoneNumber.number")
        );
        final Bson identifiersIndex = Indexes.compoundIndex(
                Indexes.ascending("domain"),
                Indexes.ascending("identifiers.identifier")
        );

        final IndexOptions emailIndexOptions = new IndexOptions()
                .unique(true)
                .partialFilterExpression(Filters.type("email.email", BsonType.STRING))
                .name("accounts.email.index");

        final IndexOptions backupEmailIndexOptions = new IndexOptions()
                .unique(true)
                .partialFilterExpression(Filters.type("backupEmail.email", BsonType.STRING))
                .name("accounts.backupEmail.index");

        final IndexOptions phoneNumberIndexOptions = new IndexOptions()
                .unique(true)
                .partialFilterExpression(Filters.type("phoneNumber.number", BsonType.STRING))
                .name("accounts.phoneNumber.index");

        final IndexOptions credentialsIndexOptions = new IndexOptions()
                .unique(true)
                .name("accounts.identifier.index");

        createIndex(accountsCollection, emailIndex, emailIndexOptions);
        createIndex(accountsCollection, backupEmailIndex, backupEmailIndexOptions);
        createIndex(accountsCollection, phoneNumberIndex, phoneNumberIndexOptions);
        createIndex(accountsCollection, identifiersIndex, credentialsIndexOptions);;
    }

    private void createIndex(final String collectionName, final Bson indexDefinition,
                             final IndexOptions indexOptions) {
        try {
            database.getCollection(collectionName)
                    .createIndex(indexDefinition, indexOptions);

            LOG.info("Created database index {}", indexOptions.getName());
        } catch (final Throwable e) {
            handleExceptions(e);
        }
    }

    private void handleExceptions(final Throwable e) {
        if (e instanceof DuplicateKeyException) {
            LOG.info("Index already exists");
        } else {
            LOG.error("An error occurred", e);
        }
    }
}
