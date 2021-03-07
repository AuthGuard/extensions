package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.model.RoleDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateRolesRepositoryTest {
    private HibernateRolesRepository repository;

    private RoleDO first;
    private RoleDO second;

    @BeforeAll
    void setup() {
        repository = new HibernateRolesRepository();

        first = repository.save(RoleDO.builder()
                .id(UUID.randomUUID().toString())
                .name("first")
                .build()).join();

        second = repository.save(RoleDO.builder()
                .id(UUID.randomUUID().toString())
                .name("second")
                .build()).join();
    }

    @Test
    void getAll() {
        assertThat(repository.getAll().join()).containsOnly(first, second);
    }

    @Test
    void getByName() {
        assertThat(repository.getByName(first.getName()).join()).contains(first);
        assertThat(repository.getByName(second.getName()).join()).contains(second);
    }

    @Test
    void getMultiple() {
        assertThat(repository.getMultiple(Arrays.asList(first.getName(), second.getName())).join())
                .containsOnly(first, second);
    }
}