package com.nexblocks.authguard.dal.couchdb.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateRolesRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CockroachDBRolesRepositoryTest extends HibernateRolesRepositoryTest {
//    @BeforeAll
//    @Override
//    public void setup() {
//        CockroachTestContainer.start();
//        initialize(CockroachTestContainer.getSessionProvider());
//    }
}
