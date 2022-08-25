package com.nexblocks.authguard.dal.mongo.persistence;

import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.EmailDO;
import com.nexblocks.authguard.dal.model.PhoneNumberDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.persistence.bootstrap.IndicesBootstrap;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
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
        final EmailDO email = EmailDO.builder()
                .email("saveAndGetById@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(UUID.randomUUID().toString()) // even if set, it should be replaced with an ObjectId
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .domain("main")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.getById(persisted.getId()).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndGetByEmail() {
        final String id = UUID.randomUUID().toString();
        final EmailDO email = EmailDO.builder()
                .email("saveAndGetByEmail@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(id)
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .domain("main")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.getByEmail(email.getEmail(), account.getDomain()).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndGetByIdentifier() {
        final EmailDO email = EmailDO.builder()
                .email("saveAndGetByIdentifier@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(UUID.randomUUID().toString()) // even if set, it should be replaced with an ObjectId
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .domain("main")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.findByIdentifier(email.getEmail(), account.getDomain())
                .join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndGetByRole() {
        final String id = UUID.randomUUID().toString();
        final AccountDO account = AccountDO.builder()
                .id(id)
                .createdAt(Instant.now())
                .roles(Collections.singleton("getByRole"))
                .permissions(Collections.emptySet())
                .domain("main")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveAndGetByRole")
                        .build()))
                .build();

        final AccountDO persisted = repository.save(account).join();
        final List<AccountDO> retrieved = repository.getByRole("getByRole", account.getDomain()).join();

        assertThat(retrieved).containsExactly(persisted);
    }

    @Test
    public void saveDuplicateEmails() {
        final EmailDO email = EmailDO.builder()
                .email("saveDuplicateEmails@test.com")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateEmailSecond")
                        .build()))
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
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullEmailsFirst")
                        .build()))
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullEmailsSecond")
                        .build()))
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
                .createdAt(Instant.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateBackupEmailSecond")
                        .build()))
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
                .createdAt(Instant.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullBackupEmailFirst")
                        .build()))
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullBackupEmailSecond")
                        .build()))
                .build();

        repository.save(first).join();
        repository.save(second).join();
    }

    @Test
    public void saveDuplicatePhoneNumbers() {
        final PhoneNumberDO phoneNumber = PhoneNumberDO.builder()
                .number("saveDuplicatePhoneNumbers")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(phoneNumber.getNumber())
                        .build()))
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicatePhoneNumbersSecond")
                        .build()))
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
                .createdAt(Instant.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullPhoneNumbersFirst")
                        .build()))
                .build();

        final AccountDO second = AccountDO.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullPhoneNumbersSecond")
                        .build()))
                .build();

        repository.save(first).join();
        repository.save(second).join();
    }
}