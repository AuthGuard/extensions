package com.nexblocks.authguard.dal.postgres.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateAccountsRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostgresAccountRepositoryTest extends HibernateAccountsRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        PostgresContainer.start();
        initialize(PostgresContainer.getSessionProvider());
    }
}
