package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.persistence.LongPage;
import com.nexblocks.authguard.dal.persistence.Page;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernatePermissionsRepositoryTest {
    private HibernatePermissionsRepository repository;

    private PermissionDO first;
    private PermissionDO second;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernatePermissionsRepository(new ReactiveQueryExecutor(sessionProvider));

        first = repository.save(PermissionDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .permissionGroup("tests")
                .name("first")
                .domain("main")
                .build())
                .subscribeAsCompletionStage().join();

        second = repository.save(PermissionDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .permissionGroup("tests")
                .name("second")
                .domain("main")
                .build())
                .subscribeAsCompletionStage().join();
    }

    @Test
    public void search() {
        assertThat(repository.search("tests", "first", "main").join()).contains(first);
        assertThat(repository.search("tests", "second", "main").join()).contains(second);
    }

    @Test
    public void getAll() {
        assertThat(repository.getAll("main", LongPage.of(null, 20)).join()).containsOnly(first, second);
    }

    @Test
    public void getAllForGroup() {
        assertThat(repository.getAllForGroup("tests", "main", LongPage.of(null, 20)).join()).containsOnly(first, second);
    }

    @Test
    public void getAllForNonexistentGroup() {
        assertThat(repository.getAllForGroup("nothing", "main", LongPage.of(null, 20)).join()).isEmpty();
    }
}