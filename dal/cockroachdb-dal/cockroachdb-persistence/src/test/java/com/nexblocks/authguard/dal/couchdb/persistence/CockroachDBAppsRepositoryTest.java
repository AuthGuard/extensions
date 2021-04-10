package com.nexblocks.authguard.dal.couchdb.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateAppsRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CockroachDBAppsRepositoryTest extends HibernateAppsRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        CockroachTestContainer.start();
        super.setup();
    }
}
