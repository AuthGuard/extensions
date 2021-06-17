package com.nexblocks.authguard.dal.cockroachdb.cache;

import com.nexblocks.authguard.dal.hibernate.cache.HibernateSessionsRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CockroachDBSessionsRepositoryTest extends HibernateSessionsRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        CockroachTestContainer.start();
        initialize(CockroachTestContainer.getSessionProvider());
    }
}