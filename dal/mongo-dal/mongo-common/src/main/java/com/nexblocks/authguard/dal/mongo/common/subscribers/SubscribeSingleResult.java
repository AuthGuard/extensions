package com.nexblocks.authguard.dal.mongo.common.subscribers;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.concurrent.CompletableFuture;

public class SubscribeSingleResult<T> extends FutureSubscriber<T, T> {
    private Subscription subscription;
    private T received;

    private SubscribeSingleResult(final CompletableFuture<T> future) {
        super(future);
    }

    public static <T> SubscribeSingleResult<T> toPublisher(final Publisher<T> publisher) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        final SubscribeSingleResult<T> subscriber = new SubscribeSingleResult<>(future);

        publisher.subscribe(subscriber);

        return subscriber;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        subscription.request(1);

        this.subscription = subscription;
    }

    @Override
    public void onNext(final T value) {
        if (this.received != null) {
            subscription.cancel();
            throw new RuntimeException("Received more than one value");
        }

        this.received = value;
    }

    @Override
    public void onError(final Throwable throwable) {
        subscription.cancel();
        future.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        future.complete(received);
    }
}
