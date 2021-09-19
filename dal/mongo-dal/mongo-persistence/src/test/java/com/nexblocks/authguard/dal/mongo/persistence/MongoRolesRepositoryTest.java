package com.nexblocks.authguard.dal.mongo.persistence;

import com.nexblocks.authguard.dal.model.RoleDO;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoRolesRepositoryTest {
    private MongoRolesRepository repository;

    private RoleDO first;
    private RoleDO second;

    @BeforeAll
    public void setup() {
        MongoDbTestContainer.start();
        final MongoClientWrapper clientWrapper = new MongoClientWrapper(MongoDbTestContainer.configuration());

        repository = new MongoRolesRepository(clientWrapper);

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
    public void getAll() {
        assertThat(repository.getAll().join()).containsOnly(first, second);
    }

    @Test
    public void getByName() {
        assertThat(repository.getByName(first.getName()).join()).contains(first);
        assertThat(repository.getByName(second.getName()).join()).contains(second);
    }

    @Test
    public void getMultiple() {
        assertThat(repository.getMultiple(Arrays.asList(first.getName(), second.getName())).join())
                .containsOnly(first, second);
    }
}