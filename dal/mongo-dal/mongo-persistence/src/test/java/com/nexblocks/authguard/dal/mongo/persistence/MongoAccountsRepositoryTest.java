package com.nexblocks.authguard.dal.mongo.persistence;

import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.EmailDO;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoAccountsRepositoryTest {
    private MongoAccountsRepository repository;

    @BeforeAll
    public void setup() {
        MongoDbTestContainer.start();

        repository = new MongoAccountsRepository(new MongoClientWrapper(MongoDbTestContainer.configuration()));
    }

    @Test
    public void saveAndGetById() {
        final String id = UUID.randomUUID().toString();
        final EmailDO email = EmailDO.builder()
                .email("saveAndGetById@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(id)
                .createdAt(OffsetDateTime.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.getById(id).join();

        assertThat(retrieved).contains(persisted);
    }
}