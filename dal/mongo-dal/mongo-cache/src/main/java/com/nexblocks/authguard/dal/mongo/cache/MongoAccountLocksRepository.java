package com.nexblocks.authguard.dal.mongo.cache;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.cache.AccountLocksRepository;
import com.nexblocks.authguard.dal.model.AccountLockDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MongoAccountLocksRepository extends AbstractMongoRepository<AccountLockDO>
        implements AccountLocksRepository {
    private static final String COLLECTION_KEY = "account_locks";

    @Inject
    public MongoAccountLocksRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.APPLICATIONS, AccountLockDO.class);
    }

    @Override
    public CompletableFuture<Collection<AccountLockDO>> findByAccountId(final long accountId) {
        return facade.find(Filters.eq("accountId", accountId))
                .subscribeAsCompletionStage()
                .thenApply(Function.identity());
    }
}
