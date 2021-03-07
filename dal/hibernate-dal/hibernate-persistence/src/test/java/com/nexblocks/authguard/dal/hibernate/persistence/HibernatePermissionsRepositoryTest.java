package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.model.PermissionDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernatePermissionsRepositoryTest {
    private HibernatePermissionsRepository repository;

    private PermissionDO first;
    private PermissionDO second;

    @BeforeAll
    void setup() {
        repository = new HibernatePermissionsRepository();

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
    void search() {
        assertThat(repository.search("tests", "first").join()).contains(first);
        assertThat(repository.search("tests", "second").join()).contains(second);
    }

    @Test
    void getAll() {
        assertThat(repository.getAll().join()).containsOnly(first, second);
    }

    @Test
    void getAllForGroup() {
        assertThat(repository.getAllForGroup("tests").join()).containsOnly(first, second);
    }

    @Test
    void getAllForNonexistentGroup() {
        assertThat(repository.getAllForGroup("nothing").join()).isEmpty();
    }
}