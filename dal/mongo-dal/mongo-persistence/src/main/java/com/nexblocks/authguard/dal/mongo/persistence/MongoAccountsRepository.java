package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoAccountsRepository extends AbstractMongoRepository<AccountDO> implements AccountsRepository {
    private static final String COLLECTION_KEY = "accounts";

    @Inject
    public MongoAccountsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.ACCOUNTS, AccountDO.class);
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByExternalId(final String externalId) {
        return facade.findOne(Filters.eq("externalId", externalId));
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByEmail(final String email) {
        return facade.findOne(Filters.eq("email.email", email));
    }

    @Override
    public CompletableFuture<List<AccountDO>> getByRole(final String role) {
        return facade.find(Filters.in("roles", role));
    }
}
