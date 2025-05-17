package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.AppDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.ApplicationsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoApplicationsRepository extends AbstractMongoRepository<AppDO> implements ApplicationsRepository {
    private static final String COLLECTION_KEY = "apps";

    @Inject
    public MongoApplicationsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.APPLICATIONS, AppDO.class);
    }

    @Override
    public CompletableFuture<Optional<AppDO>> getByExternalId(final String externalId) {
        return facade.findOne(Filters.eq("externalId", externalId))
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<List<AppDO>> getAllForAccount(final long accountId, final Page<Long> page) {
        return facade.find(Filters.and(
                Filters.eq("parentAccountId", accountId),
                Filters.gt("_id", page.getCursor())
        ), page.getCount()).subscribeAsCompletionStage();
    }
}
