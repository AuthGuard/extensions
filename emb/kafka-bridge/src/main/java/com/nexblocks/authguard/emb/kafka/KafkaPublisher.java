package com.nexblocks.authguard.emb.kafka;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nexblocks.authguard.config.ConfigContext;
import com.nexblocks.authguard.emb.model.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class KafkaPublisher {
    private final static Logger LOG = LoggerFactory.getLogger(KafkaPublisher.class);

    private final ImmutableKafkaConfiguration config;
    private final KafkaProducer<String, Message> kafkaProducer;

    @Inject
    public KafkaPublisher(@Named("kafka") final ConfigContext config) {
        this(config.asConfigBean(ImmutableKafkaConfiguration.class));
    }

    KafkaPublisher(final ImmutableKafkaConfiguration config) {
        this.config = config;
        this.kafkaProducer = KafkaProducerFactory.create(this.config);

        LOG.info("Loaded Kafka publisher with config: {}", this.config);
    }

    public void publish(final Message message) {
        final String topic = Optional.ofNullable(this.config.getTopics().get(message.getEventType().name()))
                .orElseThrow(() -> new IllegalArgumentException("Unmapped event type " + message.getEventType()));

        final ProducerRecord<String, Message> record = new ProducerRecord<>(topic, message);

        kafkaProducer.send(record, this::kafkaSendCallback);
    }

    private void kafkaSendCallback(final RecordMetadata metadata, final Exception e) {
        if (e != null) {
            LOG.error("Failed to publish event with metadata {}", metadata, e);
        }
    }
}
