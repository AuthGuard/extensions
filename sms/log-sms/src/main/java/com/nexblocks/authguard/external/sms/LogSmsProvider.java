package com.nexblocks.authguard.external.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSmsProvider implements SmsProvider {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void send(final ImmutableTextMessage textMessage) {
        log.info("Sent sms {}", textMessage);
    }
}
