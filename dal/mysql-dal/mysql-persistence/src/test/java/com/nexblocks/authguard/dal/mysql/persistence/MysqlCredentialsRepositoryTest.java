package com.nexblocks.authguard.dal.mysql.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateCredentialsRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MysqlCredentialsRepositoryTest extends HibernateCredentialsRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        MysqlTestContainer.start();
        super.setup();
    }
}
