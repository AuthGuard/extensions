package com.nexblocks.authguard.dal.mongo.common;

import com.nexblocks.authguard.dal.model.AbstractDO;
import com.nexblocks.authguard.dal.mongo.common.facade.ReactiveMongoFacade;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.ImmutableMongoConfiguration;
import io.smallrye.mutiny.Uni;

import java.util.Optional;

public abstract class AbstractMongoRepository<T extends AbstractDO> {
    protected final ReactiveMongoFacade<T> facade;

    protected AbstractMongoRepository(final ReactiveMongoFacade<T> facade) {
        this.facade = facade;
    }

    protected AbstractMongoRepository(final MongoClientWrapper clientWrapper, final String collectionKey,
                                      final String collectionDefault, final Class<T> documentType) {
        this(new ReactiveMongoFacade.Builder()
                .client(clientWrapper.getClient())
                .database(clientWrapper.getConfig().getDatabase())
                .collection(Optional.of(clientWrapper.getConfig())
                        .map(ImmutableMongoConfiguration::getCollections)
                        .map(collections -> collections.get(collectionKey))
                        .orElse(collectionDefault))
                .operationsTimeout(clientWrapper.getConfig().operationTimeout())
                .buildForType(documentType));
    }

    public Uni<T> save(final T record) {
        return facade.save(record);
    }

    public Uni<Optional<T>> getById(final long id) {
        return facade.findById(id);
    }

    public Uni<Optional<T>> update(final T record) {
        return facade.replaceById(record.getId(), record);
    }

    public Uni<Optional<T>> delete(final long id) {
        return facade.deleteById(id);
    }
}
