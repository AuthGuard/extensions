package com.nexblocks.authguard.emb.kafka;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(
        jdkOnly = true,
        validationMethod = Value.Style.ValidationMethod.NONE
)
@JsonSerialize(as = ImmutableKafkaConfiguration.class)
@JsonDeserialize(as = ImmutableKafkaConfiguration.class)
public interface KafkaConfiguration {
    String getClientId();
    List<String> getBootstrapHosts();
    Map<String, String> getTopics();
    Map<String, String> getProducerConfig();
}
