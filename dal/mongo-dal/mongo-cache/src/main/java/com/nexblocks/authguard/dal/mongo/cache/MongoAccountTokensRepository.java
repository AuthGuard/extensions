package com.nexblocks.authguard.dal.mongo.cache;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.cache.AccountTokensRepository;
import com.nexblocks.authguard.dal.model.AccountTokenDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoAccountTokensRepository extends AbstractMongoRepository<AccountTokenDO> implements AccountTokensRepository {
    private static final String COLLECTION_KEY = "account_tokens";

    @Inject
    public MongoAccountTokensRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.ACCOUNT_TOKENS, AccountTokenDO.class);
    }

    @Override
    public CompletableFuture<Optional<AccountTokenDO>> getByToken(final String token) {
        return facade.findOne(Filters.eq("token", token));
    }

    @Override
    public CompletableFuture<Optional<AccountTokenDO>> deleteToken(final String token) {
        return facade.deleteByFilter(Filters.eq("token", token));
    }
}
