package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.ApiKeyDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.ApiKeysRepository;
import io.smallrye.mutiny.Uni;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoApiKeysRepository extends AbstractMongoRepository<ApiKeyDO> implements ApiKeysRepository {
    private static final String COLLECTION_KEY = "api_keys";

    @Inject
    public MongoApiKeysRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.API_KEYS, ApiKeyDO.class);
    }

    @Override
    public Uni<Collection<ApiKeyDO>> getByAppId(final long appId) {
        return facade.find(Filters.eq("appId", appId))
                .map(list -> list);
    }

    @Override
    public Uni<Optional<ApiKeyDO>> getByKey(final String key) {
        return facade.findOne(Filters.eq("key", key));
    }
}
