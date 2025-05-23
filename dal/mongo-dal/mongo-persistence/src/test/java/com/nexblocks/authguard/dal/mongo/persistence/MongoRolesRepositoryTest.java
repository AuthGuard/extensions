package com.nexblocks.authguard.dal.mongo.persistence;

import com.nexblocks.authguard.dal.model.RoleDO;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.persistence.LongPage;
import com.nexblocks.authguard.dal.persistence.Page;
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
        assertThat(repository.getAll("main", LongPage.of(null, 20)).join()).containsOnly(first, second);
    }

    @Test
    public void getByName() {
        assertThat(repository.getByName(first.getName(), "main").join()).contains(first);
        assertThat(repository.getByName(second.getName(), "main").join()).contains(second);
    }

    @Test
    public void getMultiple() {
        assertThat(repository.getMultiple(Arrays.asList(first.getName(), second.getName()), "main").join())
                .containsOnly(first, second);
    }
}