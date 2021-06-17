package com.nexblocks.authguard.dal.hibernate.cache;

import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;

import java.util.Properties;

public class TestSessionProvider {
    static SessionProvider create() {
        return new SessionProvider(h2Properties());
    }

    private static Properties h2Properties() {
        final Properties properties = new Properties();

        properties.put("hibernate.connection.url", "jdbc:h2:mem:test_db");
        properties.put("hibernate.connection.driver_class", "org.h2.Driver");
        properties.put("hibernate.connection.username", "admin");
        properties.put("hibernate.connection.password", "mysecretpassword");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return properties;
    }
}
