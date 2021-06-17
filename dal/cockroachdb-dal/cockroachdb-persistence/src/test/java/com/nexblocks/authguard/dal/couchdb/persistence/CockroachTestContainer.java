package com.nexblocks.authguard.dal.couchdb.persistence;

import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import org.testcontainers.containers.CockroachContainer;

import java.util.Properties;

public class CockroachTestContainer {
    private static CockroachContainer container;
    private static SessionProvider sessionProvider;

    static void start() {
        if (container == null) {
            container = new CockroachContainer();
        }

        if (!container.isRunning()) {
            container.start();

            final Properties hibernateProperties = testProperties();
            hibernateProperties.put("hibernate.connection.url", container.getJdbcUrl());

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
        properties.put("hibernate.connection.username", "root");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.dialect", "org.hibernate.dialect.CockroachDB201Dialect");
        properties.put("hibernate.auto_quote_keyword", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return properties;
    }
}
