package com.nexblocks.authguard.dal.mysql.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateIdempotencyRecordRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MysqlIdempotencyRecordsRepositoryTest extends HibernateIdempotencyRecordRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        MysqlTestContainer.start();
        super.setup();
    }
}
