package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.IdempotentRecordDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.IdempotentRecordsRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoIdempotentRecordsRepository extends AbstractMongoRepository<IdempotentRecordDO>
        implements IdempotentRecordsRepository {
    private static final String COLLECTION_KEY = "idempotent_records";

    @Inject
    public MongoIdempotentRecordsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.APPLICATIONS, IdempotentRecordDO.class);
    }

    @Override
    public CompletableFuture<List<IdempotentRecordDO>> findByKey(final String key) {
        return facade.find(Filters.eq("idempotentKey", key));
    }

    @Override
    public CompletableFuture<Optional<IdempotentRecordDO>> findByKeyAndEntityType(final String key,
                                                                                  final String entityType) {
        return facade.findOne(Filters.and(Filters.eq("idempotentKey", key),
                Filters.eq("entityType", entityType)));
    }
}
