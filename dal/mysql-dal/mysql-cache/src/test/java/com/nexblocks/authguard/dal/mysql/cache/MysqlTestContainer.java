package com.nexblocks.authguard.dal.mysql.cache;

import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import org.testcontainers.containers.MySQLContainer;

import java.util.Properties;

public class MysqlTestContainer {
    private static MySQLContainer container;
    private static SessionProvider sessionProvider;

    static void start() {
        if (container == null) {
            container = new MySQLContainer()
                    .withDatabaseName("test")
                    .withUsername("admin")
                    .withPassword("secret_password");
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

        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        properties.put("hibernate.connection.username", "admin");
        properties.put("hibernate.connection.password", "secret_password");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
        properties.put("hibernate.auto_quote_keyword", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return properties;
    }
}
