package com.nexblocks.authguard.dal.mongo.cache;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.cache.SessionsRepository;
import com.nexblocks.authguard.dal.model.SessionDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoSessionsRepository extends AbstractMongoRepository<SessionDO> implements SessionsRepository {
    private static final String COLLECTION_KEY = "sessions";

    @Inject
    public MongoSessionsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.SESSIONS, SessionDO.class);
    }

    @Override
    public CompletableFuture<Optional<SessionDO>> getByToken(final String token) {
        return facade.findOne(Filters.eq("sessionToken", token));
    }

    @Override
    public CompletableFuture<Optional<SessionDO>> deleteByToken(final String sessionToken) {
        return facade.deleteByFilter(Filters.eq("sessionToken", sessionToken));
    }
}
