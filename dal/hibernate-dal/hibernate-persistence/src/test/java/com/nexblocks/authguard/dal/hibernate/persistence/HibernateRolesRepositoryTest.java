package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
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
        ReactiveQueryExecutor queryExecutor = new ReactiveQueryExecutor(sessionProvider);

        repository = new HibernateRolesRepository(queryExecutor);

        first = repository.save(RoleDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .name("first")
                .domain("main")
                .build()).subscribeAsCompletionStage().join();

        second = repository.save(RoleDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .name("second")
                .domain("main")
                .build()).subscribeAsCompletionStage().join();
    }

    @Test
    public void getAll() {
        assertThat(repository.getAll("main", LongPage.of(null, 20)).subscribeAsCompletionStage().join()).containsOnly(first, second);
    }

    @Test
    public void getByName() {
        assertThat(repository.getByName(first.getName(), first.getDomain()).subscribeAsCompletionStage().join()).contains(first);
        assertThat(repository.getByName(second.getName(), second.getDomain()).subscribeAsCompletionStage().join()).contains(second);
    }

    @Test
    public void getMultiple() {
        assertThat(repository.getMultiple(Arrays.asList(first.getName(), second.getName()), "main").subscribeAsCompletionStage().join())
                .containsOnly(first, second);
    }
}