package com.nexblocks.authguard.dal.postgres.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateRolesRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostgresRolesRepositoryTest extends HibernateRolesRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        PostgresContainer.start();
        super.setup();
    }
}
