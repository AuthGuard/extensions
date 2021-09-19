package com.nexblocks.authguard.dal.mongo.common;

import com.nexblocks.authguard.dal.model.AbstractDO;
import com.nexblocks.authguard.dal.mongo.common.facade.ReactiveMongoFacade;
import com.nexblocks.authguard.dal.mongo.common.facade.SyncMongoFacade;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.ImmutableMongoConfiguration;
import org.bson.types.ObjectId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractMongoRepository<T extends AbstractDO> {
    protected final SyncMongoFacade<T> facade;

    protected AbstractMongoRepository(final SyncMongoFacade<T> facade) {
        this.facade = facade;
    }

    protected AbstractMongoRepository(final MongoClientWrapper clientWrapper, final String collectionKey,
                                      final String collectionDefault, final Class<T> documentType) {
        this(new SyncMongoFacade.Builder()
                .client(clientWrapper.getClient())
                .database(clientWrapper.getConfig().getDatabase())
                .collection(Optional.of(clientWrapper.getConfig())
                        .map(ImmutableMongoConfiguration::getCollections)
                        .map(collections -> collections.get(collectionKey))
                        .orElse(collectionDefault))
                .operationsTimeout(clientWrapper.getConfig().operationTimeout())
                .buildForType(documentType));
    }

    public CompletableFuture<T> save(final T record) {
        if (record.getId() == null) {
            record.setId(ObjectId.get().toString());
        }

        return facade.save(record);
    }

    public CompletableFuture<Optional<T>> getById(final String id) {
        return facade.findById(id);
    }

    public CompletableFuture<Optional<T>> update(final T record) {
        return facade.replaceById(record.getId(), record);
    }

    public CompletableFuture<Optional<T>> delete(final String id) {
        return facade.deleteById(id);
    }
}
