package com.nexblocks.authguard.dal.mongo.common.subscribers;

import org.reactivestreams.Publisher;

public class WaitForCompletion {
    public static void wait(final Publisher<?> publisher) {
        SubscribeSingleResult.toPublisher(publisher)
                .getFuture()
                .join();
    }
}
