package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.nexblocks.authguard.dal.model.CryptoKeyDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.CryptoKeysRepository;
import com.nexblocks.authguard.dal.persistence.Page;
import org.bson.conversions.Bson;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MongoCryptoKeysRepository extends AbstractMongoRepository<CryptoKeyDO> implements CryptoKeysRepository {
    private static final String COLLECTION_KEY = "crypto_keys";

    @Inject
    public MongoCryptoKeysRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.CLIENTS, CryptoKeyDO.class);
    }

    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByDomain(final String domain, final Page<Instant> page) {
        Bson sort = Sorts.descending("createdAt");

        return facade.find(Filters.and(
                Filters.eq("domain", domain),
                Filters.lte("createdAt", page.getCursor())
        ), sort, page.getCount()).thenApply(Function.identity());

    }

    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByAccountId(final String domain, final long accountId,
                                                                final Page<Instant> page) {
        Bson sort = Sorts.descending("createdAt");

        return facade.find(Filters.and(
                Filters.eq("domain", domain),
                Filters.eq("accountId", accountId),
                Filters.lte("createdAt", page.getCursor())
        ), sort, page.getCount()).thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByAppId(final String domain, final long appId,
                                                            final Page<Instant> page) {
        Bson sort = Sorts.descending("createdAt");

        return facade.find(Filters.and(
                Filters.eq("domain", domain),
                Filters.eq("appId", appId),
                Filters.lte("createdAt", page.getCursor())
        ), sort, page.getCount()).thenApply(Function.identity());
    }
}
