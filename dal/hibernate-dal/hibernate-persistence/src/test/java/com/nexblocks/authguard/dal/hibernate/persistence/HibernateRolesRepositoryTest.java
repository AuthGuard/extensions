package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.RoleDO;
import com.nexblocks.authguard.dal.persistence.LongPage;
import com.nexblocks.authguard.dal.persistence.Page;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateRolesRepositoryTest {
    private HibernateRolesRepository repository;

    private RoleDO first;
    private RoleDO second;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateRolesRepository(new QueryExecutor(sessionProvider));

        first = repository.save(RoleDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .name("first")
                .domain("main")
                .build()).join();

        second = repository.save(RoleDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .name("second")
                .domain("main")
                .build()).join();
    }

    @Test
    public void getAll() {
        assertThat(repository.getAll("main", LongPage.of(null, 20)).join()).containsOnly(first, second);
    }

    @Test
    public void getByName() {
        assertThat(repository.getByName(first.getName(), first.getDomain()).join()).contains(first);
        assertThat(repository.getByName(second.getName(), second.getDomain()).join()).contains(second);
    }

    @Test
    public void getMultiple() {
        assertThat(repository.getMultiple(Arrays.asList(first.getName(), second.getName()), "main").join())
                .containsOnly(first, second);
    }
}