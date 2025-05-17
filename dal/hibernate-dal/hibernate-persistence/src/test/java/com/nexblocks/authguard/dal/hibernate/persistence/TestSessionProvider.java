package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.Properties;

public class TestSessionProvider {
    static SessionProvider create() {
        return new SessionProvider(h2Properties());
    }

    private static Properties h2Properties() {
        final Properties properties = new Properties();

        properties.put("hibernate.connection.url", "jdbc:h2:mem:test_db");
        properties.put("hibernate.connection.driver_class", "io.vertx.driver.jdbc.H2Driver");
        properties.put("hibernate.reactive.provider", "hibernate-reactive");
        properties.put("hibernate.connection.username", "admin");
        properties.put("hibernate.connection.password", "mysecretpassword");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create");
//        properties.put("hibernate.show_sql", "true");
//        properties.put("hibernate.format_sql", "true");


        return properties;
    }
}
