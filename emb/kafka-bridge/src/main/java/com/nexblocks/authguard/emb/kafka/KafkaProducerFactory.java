package com.nexblocks.authguard.emb.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaProducerFactory {

    static  <K, V> KafkaProducer<K, V> create(final ImmutableKafkaConfiguration kafkaConfiguration) {
        final Properties properties = new Properties();

        properties.putAll(kafkaConfiguration.getProducerConfig());

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }
}
