package com.nexblocks.authguard.dal.postgres.persistence;

import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer {
    private static PostgreSQLContainer container;

    static void start() {
        if (container == null) {
            container = new PostgreSQLContainer()
                    .withUsername("admin")
                    .withPassword("secret_password");
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
