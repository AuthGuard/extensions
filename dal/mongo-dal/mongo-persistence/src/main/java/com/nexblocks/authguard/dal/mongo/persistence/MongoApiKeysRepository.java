package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.ApiKeyDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.ApiKeysRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoApiKeysRepository extends AbstractMongoRepository<ApiKeyDO> implements ApiKeysRepository {
    private static final String COLLECTION_KEY = "apikeys";

    @Inject
    public MongoApiKeysRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.API_KEYS, ApiKeyDO.class);
    }

    @Override
    public CompletableFuture<Collection<ApiKeyDO>> getByAppId(final String appId) {
        return facade.find(Filters.eq("appId", appId))
                .thenApply(list -> list);
    }

    @Override
    public CompletableFuture<Optional<ApiKeyDO>> getByKey(final String key) {
        return facade.findOne(Filters.eq("key", key));
    }
}
