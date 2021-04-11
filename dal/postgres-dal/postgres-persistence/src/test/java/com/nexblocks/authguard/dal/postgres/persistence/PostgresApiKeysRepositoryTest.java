package com.nexblocks.authguard.dal.postgres.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateApiKeysRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostgresApiKeysRepositoryTest extends HibernateApiKeysRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        PostgresContainer.start();
        initialize(PostgresContainer.getSessionProvider());
    }
}
