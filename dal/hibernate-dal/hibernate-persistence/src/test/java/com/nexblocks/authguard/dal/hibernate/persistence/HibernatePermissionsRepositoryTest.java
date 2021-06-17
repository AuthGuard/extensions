package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.PermissionDO;
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
        repository = new HibernatePermissionsRepository(new QueryExecutor(sessionProvider));

        first = repository.save(PermissionDO.builder()
                .id(UUID.randomUUID().toString())
                .group("tests")
                .name("first")
                .build()).join();

        second = repository.save(PermissionDO.builder()
                .id(UUID.randomUUID().toString())
                .group("tests")
                .name("second")
                .build()).join();
    }

    @Test
    public void search() {
        assertThat(repository.search("tests", "first").join()).contains(first);
        assertThat(repository.search("tests", "second").join()).contains(second);
    }

    @Test
    public void getAll() {
        assertThat(repository.getAll().join()).containsOnly(first, second);
    }

    @Test
    public void getAllForGroup() {
        assertThat(repository.getAllForGroup("tests").join()).containsOnly(first, second);
    }

    @Test
    public void getAllForNonexistentGroup() {
        assertThat(repository.getAllForGroup("nothing").join()).isEmpty();
    }
}