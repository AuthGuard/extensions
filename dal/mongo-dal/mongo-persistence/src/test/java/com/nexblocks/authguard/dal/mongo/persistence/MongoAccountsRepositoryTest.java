package com.nexblocks.authguard.dal.mongo.persistence;

import com.mongodb.MongoWriteException;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.EmailDO;
import com.nexblocks.authguard.dal.model.PhoneNumberDO;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.persistence.bootstrap.IndicesBootstrap;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoAccountsRepositoryTest {
    private MongoAccountsRepository repository;

    @BeforeAll
    public void setup() {
        MongoDbTestContainer.start();
        final MongoClientWrapper clientWrapper = new MongoClientWrapper(MongoDbTestContainer.configuration());

        repository = new MongoAccountsRepository(clientWrapper);

        final IndicesBootstrap bootstrap = new IndicesBootstrap(clientWrapper);

        bootstrap.run();
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

    @Test
    public void saveDuplicateEmails() {
        final EmailDO email = EmailDO.builder()
                .email("saveDuplicateEmails@test.com")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        repository.save(first).join();

        assertThatThrownBy(() -> repository.save(second).join())
                .isInstanceOf(ServiceConflictException.class);
    }

    @Test
    public void saveDuplicateNullEmails() {
        final EmailDO email = EmailDO.builder()
                .build();

        final AccountDO first = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        repository.save(first).join();
        repository.save(second).join();
    }

    @Test
    public void saveDuplicateBackupEmails() {
        final EmailDO email = EmailDO.builder()
                .email("saveDuplicateBackupEmails@test.com")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        repository.save(first).join();

        assertThatThrownBy(() -> repository.save(second).join())
                .isInstanceOf(ServiceConflictException.class);
    }

    @Test
    public void saveDuplicateNullBackupEmails() {
        final EmailDO email = EmailDO.builder()
                .build();

        final AccountDO first = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        repository.save(first).join();
        repository.save(second).join();
    }

    @Test
    public void saveDuplicatePhoneNumbers() {
        final PhoneNumberDO phoneNumber = PhoneNumberDO.builder()
                .number("saveDuplicateEmails")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        repository.save(first).join();

        assertThatThrownBy(() -> repository.save(second).join())
                .isInstanceOf(ServiceConflictException.class);
    }

    @Test
    public void saveDuplicateNullPhoneNumbers() {
        final PhoneNumberDO phoneNumber = PhoneNumberDO.builder()
                .build();

        final AccountDO first = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        repository.save(first).join();
        repository.save(second).join();
    }
}