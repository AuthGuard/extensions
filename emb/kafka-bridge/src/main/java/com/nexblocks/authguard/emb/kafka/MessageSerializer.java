package com.nexblocks.authguard.emb.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexblocks.authguard.emb.model.Message;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonSerializer;

import java.util.Map;

public class MessageSerializer implements Serializer<Message> {
    private final ObjectMapper objectMapper;
    private final JsonSerializer jsonSerializer;

    public MessageSerializer() {
        this.objectMapper = new ObjectMapper();
        this.jsonSerializer = new JsonSerializer();
    }


    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
        jsonSerializer.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(final String topic, final Message message) {
        final JsonNode jsonNode = objectMapper.valueToTree(message);
        return jsonSerializer.serialize(topic, jsonNode);
    }

    @Override
    public byte[] serialize(final String topic, final Headers headers, final Message message) {
        final JsonNode jsonNode = objectMapper.valueToTree(message);
        return jsonSerializer.serialize(topic, jsonNode);
    }

    @Override
    public void close() {
        jsonSerializer.close();
    }
}
