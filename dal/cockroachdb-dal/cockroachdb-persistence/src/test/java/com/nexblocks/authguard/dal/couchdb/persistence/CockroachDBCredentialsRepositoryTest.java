package com.nexblocks.authguard.dal.couchdb.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateCredentialsRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CockroachDBCredentialsRepositoryTest extends HibernateCredentialsRepositoryTest {
//    @BeforeAll
//    @Override
//    public void setup() {
//        CockroachTestContainer.start();
//        initialize(CockroachTestContainer.getSessionProvider());
//    }
}
