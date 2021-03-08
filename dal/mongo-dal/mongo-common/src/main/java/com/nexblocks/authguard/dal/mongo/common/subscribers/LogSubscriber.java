package com.nexblocks.authguard.dal.mongo.common.subscribers;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class LogSubscriber<T> implements Subscriber<T> {
    private final String messageFormat;
    private final Logger log;
    private final Consumer<Throwable> errorCallback;

    public LogSubscriber(final String messageFormat, final Logger log, final Consumer<Throwable> errorCallback) {
        this.messageFormat = messageFormat;
        this.log = log;
        this.errorCallback = errorCallback;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        subscription.request(1);
    }

    @Override
    public void onNext(final T value) {
        log.info(messageFormat, value);
    }

    @Override
    public void onError(final Throwable throwable) {
        errorCallback.accept(throwable);
    }

    @Override
    public void onComplete() {

    }
}
