package com.nexblocks.authguard.dal.cockroachdb.cache;

import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import org.testcontainers.containers.CockroachContainer;

public class CockroachTestContainer {
    private static CockroachContainer container;

    static void start() {
        if (container == null) {
            container = new CockroachContainer();
        }

        if (!container.isRunning()) {
            container.start();
            SessionProvider.overrideProperty("hibernate.connection.url", container.getJdbcUrl());
        }
    }

    static void stop() {
        container.stop();
    }
}
