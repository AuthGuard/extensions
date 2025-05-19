package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.TotpKeyDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.TotpKeysRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MongoTotpKeysRepository  extends AbstractMongoRepository<TotpKeyDO>
        implements TotpKeysRepository {
    private static final String COLLECTION_KEY = "totp_keys";

    @Inject
    public MongoTotpKeysRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.CLIENTS, TotpKeyDO.class);
    }

    @Override
    public CompletableFuture<List<TotpKeyDO>> findByAccountId(final String domain, final long accountId) {
        return facade.find(Filters.and(
                Filters.eq("domain", domain),
                Filters.eq("accountId", accountId)
        )).subscribeAsCompletionStage();
    }
}
