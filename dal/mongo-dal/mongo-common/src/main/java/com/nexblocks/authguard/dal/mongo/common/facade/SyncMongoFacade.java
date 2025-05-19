package com.nexblocks.authguard.dal.mongo.common.facade;

//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SyncMongoFacade<T> {
//    private final MongoCollection<T> collection;
//    private final long timeout;
//
//    public SyncMongoFacade(final MongoCollection<T> collection, final long operationsTimeout) {
//        this.collection = collection;
//        this.timeout = operationsTimeout;
//    }
//
//    public CompletableFuture<T> save(final T document) {
//        final InsertOneResult result = collection.insertOne(document);
//
//        return CompletableFuture.completedFuture(document);
//    }
//
//    public CompletableFuture<Optional<T>> findById(final long id) {
//        return findOne(Filters.eq("_id", id));
//    }
//
//    public CompletableFuture<Optional<T>> findOne(final Bson filter) {
//        final T result = collection.find(filter).first();
//
//        return CompletableFuture.completedFuture(Optional.ofNullable(result));
//    }
//
//    public CompletableFuture<Optional<T>> replaceById(final long id, final T document) {
//        return replaceOne(Filters.eq("_id", id), document);
//    }
//
//    public CompletableFuture<Optional<T>> replaceOne(final Bson filter, final T document) {
//        final UpdateResult publisher = collection.replaceOne(filter, document);
//
//        if (publisher.getMatchedCount() == 0) {
//            return CompletableFuture.completedFuture(Optional.empty());
//        }
//
//        return CompletableFuture.completedFuture(Optional.of(document));
//    }
//
//    public CompletableFuture<List<T>> find(final Bson filter) {
//        final FindIterable<T> resultIterable = collection.find(filter);
//        final List<T> list = new ArrayList<>();
//
//        for (T result : resultIterable) {
//            list.add(result);
//        }
//
//        return CompletableFuture.completedFuture(list);
//    }
//
//    public CompletableFuture<List<T>> find(final Bson filter, final int limit) {
//        final FindIterable<T> resultIterable = collection.find(filter).limit(limit);
//        final List<T> list = new ArrayList<>();
//
//        for (T result : resultIterable) {
//            list.add(result);
//        }
//
//        return CompletableFuture.completedFuture(list);
//    }
//
//    public CompletableFuture<List<T>> find(final Bson filter, final Bson sort,
//                                           final int limit) {
//        final FindIterable<T> resultIterable = collection.find(filter)
//                .sort(sort)
//                .limit(limit);
//        final List<T> list = new ArrayList<>();
//
//        for (T result : resultIterable) {
//            list.add(result);
//        }
//
//        return CompletableFuture.completedFuture(list);
//    }
//
//    public CompletableFuture<List<T>> findAll() {
//        final FindIterable<T> resultIterable = collection.find();
//        final List<T> list = new ArrayList<>();
//
//        for (T result : resultIterable) {
//            list.add(result);
//        }
//
//        return CompletableFuture.completedFuture(list);
//    }
//
//    public CompletableFuture<Optional<T>> deleteById(final long id) {
//        return delete(Filters.eq("_id", id));
//    }
//
//    public CompletableFuture<Optional<T>> deleteByFilter(final Bson filter) {
//        return delete(filter);
//    }
//
//    public CompletableFuture<Optional<T>> delete(final Bson filter) {
//        final T result = collection.findOneAndDelete(filter);
//
//        return CompletableFuture.completedFuture(Optional.ofNullable(result));
//    }
//
//    public static class Builder {
//        private MongoClient mongoClient;
//        private String database;
//        private String collection;
//        private long operationsTimeout = Defaults.TIMEOUT;
//
//        public SyncMongoFacade.Builder client(final MongoClient mongoClient) {
//            this.mongoClient = mongoClient;
//            return this;
//        }
//
//        public SyncMongoFacade.Builder database(final String database) {
//            this.database = database;
//            return this;
//        }
//
//        public SyncMongoFacade.Builder collection(final String collection) {
//            this.collection = collection;
//            return this;
//        }
//
//        public SyncMongoFacade.Builder operationsTimeout(final long operationsTimeout) {
//            this.operationsTimeout = operationsTimeout;
//            return this;
//        }
//
//        public <T> SyncMongoFacade<T> buildForType(final Class<T> documentType) {
//            Objects.requireNonNull(mongoClient, "The client cannot be null");
//            Objects.requireNonNull(database, "A database name must be provided");
//            Objects.requireNonNull(collection, "A collection name must be provided");
//
//            final MongoCollection<T> mongoCollection = mongoClient.getDatabase(database)
//                    .getCollection(collection, documentType);
//
//            return new SyncMongoFacade<>(mongoCollection, operationsTimeout);
//        }
//    }
}
