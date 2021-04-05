package com.nexblocks.authguard.dal.postgres.cache;

import com.nexblocks.authguard.dal.hibernate.cache.HibernateSessionsRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostgresSessionsRepositoryTest extends HibernateSessionsRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        PostgresContainer.start();
        super.setup();
    }
}