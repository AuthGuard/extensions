package com.nexblocks.authguard.dal.mysql.cache;

import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import org.testcontainers.containers.MySQLContainer;

public class MysqlTestContainer {
    private static MySQLContainer container;

    static void start() {
        if (container == null) {
            container = new MySQLContainer()
                    .withDatabaseName("test")
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
