package com.nexblocks.authguard.dal.mysql.cache;

import com.nexblocks.authguard.dal.hibernate.cache.HibernateAccountTokensRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MysqlAccountTokensRepositoryTest extends HibernateAccountTokensRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        MysqlTestContainer.start();
        initialize(MysqlTestContainer.getSessionProvider());
    }
}