package com.nexblocks.authguard.dal.postgres.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateAppsRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostgresAppsRepositoryTest extends HibernateAppsRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        PostgresContainer.start();
        super.setup();
    }
}
