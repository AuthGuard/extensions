package com.nexblocks.authguard.dal.mongo.common.facade;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.nexblocks.authguard.dal.mongo.common.subscribers.SubscribeMultipleResults;
import com.nexblocks.authguard.dal.mongo.common.subscribers.SubscribeSingleResult;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MongoFacade<T> {
    private final MongoCollection<T> collection;
    private final long timeout;

    public MongoFacade(final MongoCollection<T> collection, final long operationsTimeout) {
        this.collection = collection;
        this.timeout = operationsTimeout;
    }

    public CompletableFuture<T> save(final T document) {
        final Publisher<InsertOneResult> publisher = collection.insertOne(document);
        final SubscribeSingleResult<InsertOneResult> subscriber = SubscribeSingleResult.toPublisher(publisher);

        return subscriber.getFuture()
                .orTimeout(timeout, TimeUnit.MILLISECONDS)
                .thenApply(result -> document);
    }

    public CompletableFuture<Optional<T>> findById(final String id) {
        return findOne(Filters.eq("_id", id));
    }

    public CompletableFuture<Optional<T>> findOne(final Bson filter) {
        final Publisher<T> publisher = collection.find(filter)
                .first();

        final SubscribeSingleResult<T> subscriber = SubscribeSingleResult.toPublisher(publisher);

        return subscriber.getFuture()
                .orTimeout(timeout, TimeUnit.MILLISECONDS)
                .thenApply(Optional::ofNullable);
    }

    public CompletableFuture<Optional<T>> replaceById(final String id, final T document) {
        return replaceOne(Filters.eq("_id", id), document);
    }

    public CompletableFuture<Optional<T>> replaceOne(final Bson filter, final T document) {
        final Publisher<UpdateResult> publisher = collection.replaceOne(filter, document);
        final SubscribeSingleResult<UpdateResult> subscriber = SubscribeSingleResult.toPublisher(publisher);

        return subscriber.getFuture()
                .orTimeout(timeout, TimeUnit.MILLISECONDS)
                .thenApply(result -> {
                    if (result.getMatchedCount() == 0) {
                        return Optional.empty();
                    } else {
                        return Optional.of(document);
                    }
                });
    }

    public CompletableFuture<List<T>> find(final Bson filter) {
        final FindPublisher<T> publisher = collection.find(filter);
        final SubscribeMultipleResults<T> subscriber = SubscribeMultipleResults.toPublisher(publisher);

        return subscriber.getFuture();
    }

    public CompletableFuture<List<T>> findAll() {
        final FindPublisher<T> publisher = collection.find();
        final SubscribeMultipleResults<T> subscriber = SubscribeMultipleResults.toPublisher(publisher);

        return subscriber.getFuture();
    }

    public CompletableFuture<Optional<T>> deleteById(final String id) {
        return delete(Filters.eq("_id", id));
    }

    public CompletableFuture<Optional<T>> deleteByFilter(final Bson filter) {
        return delete(filter);
    }

    public CompletableFuture<Optional<T>> delete(final Bson filter) {
        final Publisher<T> publisher = collection.findOneAndDelete(filter);
        final SubscribeSingleResult<T> subscriber = SubscribeSingleResult.toPublisher(publisher);

        return subscriber.getFuture()
                .thenApply(Optional::ofNullable);
    }

    public static class Builder {
        private MongoClient mongoClient;
        private String database;
        private String collection;
        private long operationsTimeout = Defaults.TIMEOUT;

        public Builder client(final MongoClient mongoClient) {
            this.mongoClient = mongoClient;
            return this;
        }

        public Builder database(final String database) {
            this.database = database;
            return this;
        }

        public Builder collection(final String collection) {
            this.collection = collection;
            return this;
        }

        public Builder operationsTimeout(final long operationsTimeout) {
            this.operationsTimeout = operationsTimeout;
            return this;
        }

        public <T> MongoFacade<T> buildForType(final Class<T> documentType) {
            Objects.requireNonNull(mongoClient, "The client cannot be null");
            Objects.requireNonNull(database, "A database name must be provided");
            Objects.requireNonNull(collection, "A collection name must be provided");

            final MongoCollection<T> mongoCollection = mongoClient.getDatabase(database)
                    .getCollection(collection, documentType);

            return new MongoFacade<>(mongoCollection, operationsTimeout);
        }
    }
}
