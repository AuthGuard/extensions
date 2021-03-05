package com.nexblocks.authguard.emb.kafka;

import com.google.inject.Inject;
import com.nexblocks.authguard.emb.MessageSubscriber;
import com.nexblocks.authguard.emb.annotations.Channel;
import com.nexblocks.authguard.emb.model.Message;

@Channel("*")
public class KafkaBridge implements MessageSubscriber {
    private final KafkaPublisher kafkaPublisher;

    @Inject
    public KafkaBridge(final KafkaPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    @Override
    public void onMessage(final Message message) {
        kafkaPublisher.publish(message);
    }
}
