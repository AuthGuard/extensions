package com.nexblocks.authguard.dal.postgres.cache;

import com.nexblocks.authguard.dal.hibernate.HibernateAccountTokensRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostgresAccountTokensRepositoryTest extends HibernateAccountTokensRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        PostgresContainer.start();
        super.setup();
    }
}