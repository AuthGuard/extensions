package com.nexblocks.authguard.emb.log;

import com.nexblocks.authguard.emb.MessageSubscriber;
import com.nexblocks.authguard.emb.annotations.Channel;
import com.nexblocks.authguard.emb.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Channel("*")
public class LogMessagePublisher implements MessageSubscriber {
    private final static Logger LOG = LoggerFactory.getLogger(LogMessagePublisher.class);

    @Override
    public void onMessage(final Message message) {
        LOG.info("Received {}", message);
    }
}
