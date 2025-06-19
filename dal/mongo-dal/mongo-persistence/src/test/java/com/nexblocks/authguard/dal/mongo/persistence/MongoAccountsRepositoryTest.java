package com.nexblocks.authguard.dal.mongo.persistence;

import com.nexblocks.authguard.dal.model.*;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.persistence.bootstrap.IndicesBootstrap;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.util.*;

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

        bootstrap.run().subscribeAsCompletionStage().join();
    }

    @Test
    public void saveAndGetById() {
        final EmailDO email = EmailDO.builder()
                .email("saveAndGetById@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits())) // even if set, it should be replaced with an ObjectId
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .domain("main")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .build();

        final AccountDO persisted = repository.save(account).subscribeAsCompletionStage().join();
        final Optional<AccountDO> retrieved = repository.getById(persisted.getId()).subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndGetByEmail() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
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

        final AccountDO persisted = repository.save(account).subscribeAsCompletionStage().join();
        final Optional<AccountDO> retrieved = repository.getByEmail(email.getEmail(), account.getDomain()).subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndGetByIdentifier() {
        final EmailDO email = EmailDO.builder()
                .email("saveAndGetByIdentifier@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits())) // even if set, it should be replaced with an ObjectId
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .domain("main")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .build();

        final AccountDO persisted = repository.save(account).subscribeAsCompletionStage().join();
        final Optional<AccountDO> retrieved = repository.findByIdentifier(email.getEmail(), account.getDomain()).subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void saveAndGetByRole() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
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

        final AccountDO persisted = repository.save(account).subscribeAsCompletionStage().join();
        final List<AccountDO> retrieved = repository.getByRole("getByRole", account.getDomain()).subscribeAsCompletionStage().join();

        assertThat(retrieved).containsExactly(persisted);
    }

    @Test
    public void saveDuplicateEmails() {
        final EmailDO email = EmailDO.builder()
                .email("saveDuplicateEmails@test.com")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .domain("main")
                .build();

        final AccountDO second = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateEmailSecond")
                        .build()))
                .domain("main")
                .build();

        repository.save(first).subscribeAsCompletionStage().join();

        assertThatThrownBy(() -> repository.save(second).subscribeAsCompletionStage().join())
                .hasCauseInstanceOf(ServiceConflictException.class);
    }

    @Test
    public void saveDuplicateNullEmails() {
        final EmailDO email = EmailDO.builder()
                .build();

        final AccountDO first = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullEmailsFirst")
                        .build()))
                .domain("main")
                .build();

        final AccountDO second = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullEmailsSecond")
                        .build()))
                .domain("main")
                .build();

        repository.save(first).subscribeAsCompletionStage().join();
        repository.save(second).subscribeAsCompletionStage().join();
    }

    @Test
    public void saveDuplicateBackupEmails() {
        final EmailDO email = EmailDO.builder()
                .email("saveDuplicateBackupEmails@test.com")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(email.getEmail())
                        .build()))
                .domain("main")
                .build();

        final AccountDO second = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateBackupEmailSecond")
                        .build()))
                .domain("main")
                .build();

        repository.save(first).subscribeAsCompletionStage().join();

        assertThatThrownBy(() -> repository.save(second).subscribeAsCompletionStage().join())
                .hasCauseInstanceOf(ServiceConflictException.class);
    }

    @Test
    public void saveDuplicateNullBackupEmails() {
        final EmailDO email = EmailDO.builder()
                .build();

        final AccountDO first = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullBackupEmailFirst")
                        .build()))
                .domain("main")
                .build();

        final AccountDO second = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .backupEmail(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullBackupEmailSecond")
                        .build()))
                .domain("main")
                .build();

        repository.save(first).subscribeAsCompletionStage().join();
        repository.save(second).subscribeAsCompletionStage().join();
    }

    @Test
    public void saveDuplicatePhoneNumbers() {
        final PhoneNumberDO phoneNumber = PhoneNumberDO.builder()
                .number("saveDuplicatePhoneNumbers")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(phoneNumber.getNumber())
                        .build()))
                .domain("main")
                .build();

        final AccountDO second = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicatePhoneNumbersSecond")
                        .build()))
                .domain("main")
                .build();

        repository.save(first).subscribeAsCompletionStage().join();

        assertThatThrownBy(() -> repository.save(second).subscribeAsCompletionStage().join())
                .hasCauseInstanceOf(ServiceConflictException.class);
    }

    @Test
    public void saveDuplicateNullPhoneNumbers() {
        final PhoneNumberDO phoneNumber = PhoneNumberDO.builder()
                .build();

        final AccountDO first = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullPhoneNumbersFirst")
                        .build()))
                .domain("main")
                .build();

        final AccountDO second = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .phoneNumber(phoneNumber)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveDuplicateNullPhoneNumbersSecond")
                        .build()))
                .domain("main")
                .build();

        repository.save(first).subscribeAsCompletionStage().join();
        repository.save(second).subscribeAsCompletionStage().join();
    }

    @Test
    public void updateDuplicateEmails() {
        final EmailDO firstEmail = EmailDO.builder()
                .email("updateDuplicateEmailsFirst@test.com")
                .build();

        final EmailDO secondEmail = EmailDO.builder()
                .email("updateDuplicateEmailsSecond@test.com")
                .build();

        final AccountDO first = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .email(firstEmail)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(firstEmail.getEmail())
                        .build()))
                .domain("main")
                .build();

        final AccountDO second = AccountDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .createdAt(Instant.now())
                .email(secondEmail)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("updateDuplicateEmailsSecond")
                        .build()))
                .domain("main")
                .build();

        repository.save(first).subscribeAsCompletionStage().join();
        repository.save(second).subscribeAsCompletionStage().join();

        second.setEmail(firstEmail);

        assertThatThrownBy(() -> repository.update(second).subscribeAsCompletionStage().join())
                .hasCauseInstanceOf(ServiceConflictException.class);
    }

    @Test
    public void addAndRemoveAccountPermission() {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        String identifier = "addAccountPermission";

        AccountDO account = AccountDO.builder()
                .id(id)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .domain("main")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .type(UserIdentifierDO.Type.USERNAME)
                        .domain("main")
                        .build()))
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        AccountDO persisted = repository.save(account).subscribeAsCompletionStage().join();

        PermissionDO firstPermission = PermissionDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .domain("main")
                .forAccounts(true)
                .permissionGroup("tests")
                .name("read")
                .build();

        PermissionDO secondPermission = PermissionDO.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .domain("main")
                .forAccounts(true)
                .permissionGroup("tests")
                .name("write")
                .build();

        // add
        repository.addAccountPermissions(persisted, Arrays.asList(firstPermission, secondPermission))
                .subscribeAsCompletionStage()
                .join();

        Optional<AccountDO> afterAdd = repository.getById(persisted.getId()).subscribeAsCompletionStage().join();

        assertThat(afterAdd).isNotEmpty();
        assertThat(afterAdd.get().getPermissions())
                .containsExactlyInAnyOrder(firstPermission, secondPermission);

        // remove one
        repository.removeAccountPermissions(persisted, Collections.singletonList(firstPermission))
                .subscribeAsCompletionStage()
                .join();

        Optional<AccountDO> afterRemove = repository.getById(persisted.getId()).subscribeAsCompletionStage().join();

        assertThat(afterRemove).isNotEmpty();
        assertThat(afterRemove.get().getPermissions())
                .containsExactlyInAnyOrder(secondPermission);
    }
}