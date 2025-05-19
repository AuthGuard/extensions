package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.CredentialsAuditDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.CredentialsAuditRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoCredentialsAuditRepository extends AbstractMongoRepository<CredentialsAuditDO> implements CredentialsAuditRepository {
    private static final String COLLECTION_KEY = "credentials_audit";

    @Inject
    public MongoCredentialsAuditRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.CREDENTIALS_AUDIT, CredentialsAuditDO.class);
    }

    @Override
    public CompletableFuture<List<CredentialsAuditDO>> findByCredentialsId(final long credentialsId) {
        return facade.find(Filters.eq("credentialsId", credentialsId))
                .subscribeAsCompletionStage();
    }
}
