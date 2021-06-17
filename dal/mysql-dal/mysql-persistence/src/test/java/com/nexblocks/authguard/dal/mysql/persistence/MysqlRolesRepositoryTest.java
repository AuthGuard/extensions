package com.nexblocks.authguard.dal.mysql.persistence;

import com.nexblocks.authguard.dal.hibernate.persistence.HibernateRolesRepositoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MysqlRolesRepositoryTest extends HibernateRolesRepositoryTest {
    @BeforeAll
    @Override
    public void setup() {
        MysqlTestContainer.start();
        initialize(MysqlTestContainer.getSessionProvider());
    }
}
