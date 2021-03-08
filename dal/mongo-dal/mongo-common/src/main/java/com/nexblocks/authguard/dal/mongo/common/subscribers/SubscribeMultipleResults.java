package com.nexblocks.authguard.dal.mongo.common.subscribers;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SubscribeMultipleResults<T> extends FutureSubscriber<T, List<T>> {
    private Subscription subscription;
    private final List<T> received;

    private SubscribeMultipleResults(final CompletableFuture<List<T>> future) {
        super(future);
        this.received = new ArrayList<>();
    }

    public static <T> SubscribeMultipleResults<T> toPublisher(final Publisher<T> publisher) {
        final CompletableFuture<List<T>> future = new CompletableFuture<>();
        final SubscribeMultipleResults<T> subscriber = new SubscribeMultipleResults<>(future);

        publisher.subscribe(subscriber);

        return subscriber;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        subscription.request(Integer.MAX_VALUE);

        this.subscription = subscription;
    }

    @Override
    public void onNext(final T document) {
        this.received.add(document);
    }

    @Override
    public void onError(final Throwable throwable) {
        this.subscription.cancel();

        this.future.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        this.future.complete(received);
    }
}
