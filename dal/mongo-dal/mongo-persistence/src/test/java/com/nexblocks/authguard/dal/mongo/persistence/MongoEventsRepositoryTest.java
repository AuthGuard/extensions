package com.nexblocks.authguard.dal.mongo.persistence;

import com.nexblocks.authguard.dal.model.EventDO;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.persistence.bootstrap.IndicesBootstrap;
import com.nexblocks.authguard.dal.persistence.LongPage;
import com.nexblocks.authguard.dal.persistence.Page;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoEventsRepositoryTest {
    private MongoEventsRepository repository;

    private EventDO first;
    private EventDO second;

    @BeforeAll
    public void setup() {
        MongoDbTestContainer.start();
        final MongoClientWrapper clientWrapper = new MongoClientWrapper(MongoDbTestContainer.configuration());

        repository = new MongoEventsRepository(clientWrapper);

        final IndicesBootstrap bootstrap = new IndicesBootstrap(clientWrapper);

        bootstrap.run().subscribeAsCompletionStage().join();

        initialize();
    }

    protected void initialize() {
        first = repository.save(EventDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .domain("main")
                .channel("roles")
                .createdAt(Instant.now().minus(Duration.ofHours(3)))
                .build()).subscribeAsCompletionStage().join();

        second = repository.save(EventDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .domain("main")
                .channel("roles")
                .createdAt(Instant.now().minus(Duration.ofHours(1)))
                .build()).subscribeAsCompletionStage().join();
    }

    @Test
    void findByDomainDescending() {
        Instant cursor = Instant.now().plus(Duration.ofDays(100 * 365));

        List<EventDO> events = repository.findByDomainDescending("main", Page.of(cursor, 10, cursor))
                .subscribeAsCompletionStage()
                .join();

        assertThat(events).hasSize(2);
    }

    @Test
    void findByDomainAndChannelDescending() {
    }
}