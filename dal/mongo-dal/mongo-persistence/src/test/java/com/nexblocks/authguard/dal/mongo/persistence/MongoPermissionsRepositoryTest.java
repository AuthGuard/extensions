package com.nexblocks.authguard.dal.mongo.persistence;

import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.persistence.bootstrap.IndicesBootstrap;
import com.nexblocks.authguard.dal.persistence.LongPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoPermissionsRepositoryTest {
    private MongoPermissionsRepository repository;

    private PermissionDO first;
    private PermissionDO second;

    @BeforeAll
    public void setup() {
        MongoDbTestContainer.start();
        final MongoClientWrapper clientWrapper = new MongoClientWrapper(MongoDbTestContainer.configuration());

        repository = new MongoPermissionsRepository(clientWrapper);

        final IndicesBootstrap bootstrap = new IndicesBootstrap(clientWrapper);

        bootstrap.run().subscribeAsCompletionStage().join();

        initialize();
    }

    protected void initialize() {
        first = repository.save(PermissionDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .permissionGroup("tests")
                .name("first")
                .domain("main")
                .build()).subscribeAsCompletionStage().join();

        second = repository.save(PermissionDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .permissionGroup("tests")
                .name("second")
                .domain("main")
                .build()).subscribeAsCompletionStage().join();
    }

    @Test
    public void search() {
        assertThat(repository.search("tests", "first", "main").subscribeAsCompletionStage().join()).contains(first);
        assertThat(repository.search("tests", "second", "main").subscribeAsCompletionStage().join()).contains(second);
    }

    @Test
    public void getAll() {
        assertThat(repository.getAll("main", LongPage.of(null, 20)).subscribeAsCompletionStage().join()).containsOnly(first, second);
    }

    @Test
    public void getAllForGroup() {
        assertThat(repository.getAllForGroup("tests", "main", LongPage.of(null, 20)).subscribeAsCompletionStage().join()).containsOnly(first, second);
    }

    @Test
    public void getAllForNonexistentGroup() {
        assertThat(repository.getAllForGroup("nothing", "main", LongPage.of(null, 20)).subscribeAsCompletionStage().join()).isEmpty();
    }
}