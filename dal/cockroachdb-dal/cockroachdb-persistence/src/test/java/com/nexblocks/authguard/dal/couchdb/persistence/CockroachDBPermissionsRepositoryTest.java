package com.nexblocks.authguard.dal.couchdb.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernatePermissionsRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CockroachDBPermissionsRepositoryTest extends HibernatePermissionsRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        CockroachTestContainer.start();
        super.setup();
    }
}
