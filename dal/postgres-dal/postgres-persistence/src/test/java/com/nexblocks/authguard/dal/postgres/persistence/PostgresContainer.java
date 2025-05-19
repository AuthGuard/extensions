package com.nexblocks.authguard.dal.postgres.persistence;

import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Properties;

public class PostgresContainer {
    private static PostgreSQLContainer container;
    private static SessionProvider sessionProvider;

    static void start() {
        if (container == null) {
            container = new PostgreSQLContainer("postgres:15")
                    .withUsername("admin")
                    .withPassword("secret_password");
        }

        if (!container.isRunning()) {
            container.start();

            final Properties hibernateProperties = testProperties();
            hibernateProperties.put("hibernate.connection.url", container.getJdbcUrl());
            hibernateProperties.put("hibernate.reactive.url", container.getJdbcUrl());

            sessionProvider = new SessionProvider(hibernateProperties);
        }
    }

    static SessionProvider getSessionProvider() {
        return sessionProvider;
    }

    static void stop() {
        container.stop();
    }

    private static Properties testProperties() {
        final Properties properties = new Properties();

        properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        properties.put("hibernate.connection.username", "admin");
        properties.put("hibernate.connection.password", "secret_password");
        properties.put("hibernate.reactive.username", "admin");
        properties.put("hibernate.reactive.password", "secret_password");

        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("javax.persistence.schema-generation.database.action", "drop-and-create");

        return properties;
    }
}
