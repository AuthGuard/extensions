package com.nexblocks.authguard.external.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEmailProvider implements EmailProvider {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void send(final ImmutableEmail email) {
        log.info("Sent email {}", email);
    }
}
