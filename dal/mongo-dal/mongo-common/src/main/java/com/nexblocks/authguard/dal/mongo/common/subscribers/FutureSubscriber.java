package com.nexblocks.authguard.dal.mongo.common.subscribers;

import org.reactivestreams.Subscriber;

import java.util.concurrent.CompletableFuture;

public abstract class FutureSubscriber<T, F> implements Subscriber<T> {
    protected final CompletableFuture<F> future;

    protected FutureSubscriber(final CompletableFuture<F> future) {
        this.future = future;
    }

    public CompletableFuture<F> getFuture() {
        return this.future;
    }
}
