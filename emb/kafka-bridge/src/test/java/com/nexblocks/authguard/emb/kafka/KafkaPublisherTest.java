package com.nexblocks.authguard.emb.kafka;


import com.nexblocks.authguard.config.ConfigContext;
import com.nexblocks.authguard.config.JacksonConfigContext;
import com.nexblocks.authguard.emb.model.EventType;
import com.nexblocks.authguard.emb.model.Message;
import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// TODO those tests no longer run and need to be updated
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaPublisherTest {
    /**
     * We have a single embedded Kafka server that gets started when this test class is initialized.
     *
     * It's automatically started before any methods are run via the @RegisterExtension annotation.
     * It's automatically stopped after all of the tests are completed via the @RegisterExtension annotation.
     */
//    @RegisterExtension
    static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource();

    private ImmutableKafkaConfiguration immutableKafkaConfiguration;

//    @BeforeAll
    void setup() {
        sharedKafkaTestResource.getKafkaTestUtils()
                .createTopic("test.event", 1, (short)1);

        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = Optional.ofNullable(classLoader.getResource("application.json"))
                .map(URL::getFile)
                .map(File::new)
                .orElseThrow();

        final ConfigContext configContext = new JacksonConfigContext(file);

        immutableKafkaConfiguration = ImmutableKafkaConfiguration.builder().from(configContext.getAsConfigBean("kafka", ImmutableKafkaConfiguration.class))
                .putProducerConfig(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, sharedKafkaTestResource.getKafkaBrokers().asList().get(0).getConnectString())
                .build();
    }

//    @Test
    void publish() {
        final String topic = "test.event";

        final KafkaPublisher publisher = new KafkaPublisher(immutableKafkaConfiguration);

        final Message message = Message.builder()
                .eventType(EventType.AUTHENTICATION)
                .timestamp(Instant.now())
                .messageBody("done")
                .build();

        publisher.publish(message);

        final List<ConsumerRecord<byte[], byte[]>> consumerRecords = sharedKafkaTestResource.getKafkaTestUtils()
                .consumeAllRecordsFromTopic(topic);

        assertThat(consumerRecords).hasSize(1);

        final MessageSerializer serializer = new MessageSerializer();

        byte[] serialized = serializer.serialize(topic, message);

        assertThat(serialized).isEqualTo(consumerRecords.get(0).value());
    }
}