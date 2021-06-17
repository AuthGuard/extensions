package com.nexblocks.authguard.dal.couchdb.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateIdempotencyRecordRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CockroachDBIdempotencyRecordsRepositoryTest extends HibernateIdempotencyRecordRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        CockroachTestContainer.start();
        initialize(CockroachTestContainer.getSessionProvider());
    }
}
