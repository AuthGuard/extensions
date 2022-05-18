package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.common.collect.ImmutableSet;
import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.persistence.bootstrap.IndicesBootstrap;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoCredentialsRepositoryTest {
    private MongoCredentialsRepostiory repository;

//    @BeforeAll
    public void setup() {
        MongoDbTestContainer.start();
        final MongoClientWrapper clientWrapper = new MongoClientWrapper(MongoDbTestContainer.configuration());

        repository = new MongoCredentialsRepostiory(clientWrapper);

        final IndicesBootstrap bootstrap = new IndicesBootstrap(clientWrapper);

        bootstrap.run();
    }

//    @Test
    public void saveAndGetByIdentifier() {
        final String identifier = "saveAndGetByIdentifier";

        final CredentialsDO credentials = CredentialsDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .identifiers(ImmutableSet.of(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .domain("main")
                        .build()))
                .build();

        final CredentialsDO persisted = repository.save(credentials).join();
        final Optional<CredentialsDO> retrieved = repository.findByIdentifier(identifier, "main").join();

        assertThat(retrieved).contains(persisted);
    }

//    @Test
    public void saveDuplicateIdentifiers() {
        final String identifier = "saveDuplicateIdentifiers";

        final CredentialsDO first = CredentialsDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .identifiers(ImmutableSet.of(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .build()))
                .build();

        final CredentialsDO second = CredentialsDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .identifiers(ImmutableSet.of(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .build()))
                .build();

        repository.save(first).join();

        assertThatThrownBy(() -> repository.save(second))
                .isInstanceOf(ServiceConflictException.class);
    }
}
