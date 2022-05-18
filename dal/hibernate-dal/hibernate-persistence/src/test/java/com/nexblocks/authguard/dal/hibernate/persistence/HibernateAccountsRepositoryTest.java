package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateAccountsRepositoryTest {
    private HibernateAccountsRepository repository;
    private HibernateUserIdentifiersRepository userIdentifiersRepository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        final QueryExecutor queryExecutor = new QueryExecutor(sessionProvider);

        repository = new HibernateAccountsRepository(queryExecutor);
        userIdentifiersRepository = new HibernateUserIdentifiersRepository(queryExecutor);
    }

    @Test
    public void saveAndGetById() {
        final String id = UUID.randomUUID().toString();
        final EmailDO email = EmailDO.builder()
                .email("saveAndGetById@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .domain("main")
                .identifiers(Collections.singleton(
                        UserIdentifierDO.builder()
                                .identifier(email.getEmail())
                                .type(UserIdentifierDO.Type.EMAIL)
                                .build()
                ))
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.getById(id).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void getByExternalId() {
        final String id = UUID.randomUUID().toString();
        final String externalId = UUID.randomUUID().toString();

        final EmailDO email = EmailDO.builder()
                .email("getByExternalId@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(id)
                .externalId(externalId)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .identifiers(Collections.singleton(
                        UserIdentifierDO.builder()
                                .identifier(email.getEmail())
                                .type(UserIdentifierDO.Type.EMAIL)
                                .build()
                ))
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.getByExternalId(externalId).join();

        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved.get()).isEqualTo(persisted);
    }

    @Test
    public void getByEmail() {
        final String id = UUID.randomUUID().toString();

        final EmailDO email = EmailDO.builder()
                .email("getByEmaild@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .domain("main")
                .identifiers(Collections.singleton(
                        UserIdentifierDO.builder()
                                .identifier(email.getEmail())
                                .type(UserIdentifierDO.Type.EMAIL)
                                .build()
                ))
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.getByEmail(email.getEmail(), account.getDomain()).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void findByIdentifier() {
        final String id = UUID.randomUUID().toString();
        final String identifier = "accountsFindByIdentifier";

        final AccountDO account = AccountDO.builder()
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

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.findByIdentifier(identifier, "main").join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void getByRole() {
        final String id = UUID.randomUUID().toString();
        final String role = "test-role";

        final EmailDO email = EmailDO.builder()
                .email("getByRole@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.singleton(role))
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .domain("main")
                .identifiers(Collections.singleton(
                        UserIdentifierDO.builder()
                                .identifier(email.getEmail())
                                .type(UserIdentifierDO.Type.EMAIL)
                                .build()
                ))
                .build();

        final AccountDO persisted = repository.save(account).join();
        final List<AccountDO> retrieved = repository.getByRole(role, account.getDomain()).join();

        assertThat(retrieved).containsOnly(persisted);
    }

    @Test
    public void updateEmail() {
        final String id = UUID.randomUUID().toString();

        final EmailDO email = EmailDO.builder()
                .email("updateEmail@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.emptySet())
                .build();

        final AccountDO newAccount = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .identifiers(Collections.emptySet())
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.update(newAccount).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void updatePassword() {
        final String id = UUID.randomUUID().toString();
        final String identifier = "updatePassword";
        final EmailDO email = EmailDO.builder()
                .email("updatePassword@test.com")
                .build();

        final AccountDO credentials = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .type(UserIdentifierDO.Type.USERNAME)
                        .build()))
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        final AccountDO newCredentials = AccountDO.builder()
                .id(credentials.getId())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .identifiers(credentials.getIdentifiers())
                .hashedPassword(PasswordDO.builder()
                        .password("new_password")
                        .salt("salt")
                        .build())
                .build();

        final AccountDO persisted = repository.save(credentials).join();

        final Optional<AccountDO> updated = repository.update(newCredentials).join();
        final Optional<AccountDO> retrieved = repository.getById(credentials.getId()).join();

        assertThat(updated).contains(newCredentials);
        assertThat(retrieved).contains(newCredentials);
    }

    @Test
    public void removeIdentifier() {
        final String id = UUID.randomUUID().toString();
        final String identifier = "removeIdentifier";

        final EmailDO email = EmailDO.builder()
                .email("removeIdentifier@test.com")
                .build();

        final AccountDO credentials = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .type(UserIdentifierDO.Type.USERNAME)
                        .build()))
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        final AccountDO newCredentials = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .identifiers(Collections.emptySet())
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        repository.save(credentials).join();

        final Optional<AccountDO> updated = repository.update(newCredentials).join();
        final Optional<AccountDO> retrieved = repository.getById(credentials.getId()).join();

        final List<UserIdentifierDO> all = userIdentifiersRepository.getAll();

        assertThat(updated).contains(newCredentials);
        assertThat(retrieved).contains(newCredentials);
        assertThat(all).doesNotContain(UserIdentifierDO.builder()
                .identifier(identifier)
                .type(UserIdentifierDO.Type.USERNAME)
                .build());
    }

    @Test
    public void updateIdentifier() {
        final String id = UUID.randomUUID().toString();
        final String identifier = "updateIdentifier";
        final String newIdentifier = "updateIdentifierNew";

        final EmailDO email = EmailDO.builder()
                .email("updateIdentifier@test.com")
                .build();

        final AccountDO credentials = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .type(UserIdentifierDO.Type.USERNAME)
                        .build()))
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        repository.save(credentials).join();

        final AccountDO beforeUpdate = repository.getById(credentials.getId())
                .join()
                .get();

        final AccountDO newCredentials = AccountDO.builder()
                .id(beforeUpdate.getId())
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .metadata(Collections.emptyMap())
                .identifiers(beforeUpdate.getIdentifiers()
                        .stream()
                        .map(existing -> UserIdentifierDO.builder()
                                .type(existing.getType())
                                .identifier(newIdentifier)
                                .build())
                        .collect(Collectors.toSet())
                )
                .hashedPassword(beforeUpdate.getHashedPassword())
                .build();

        final Optional<AccountDO> updated = repository.update(newCredentials).join();
        final Optional<AccountDO> retrieved = repository.getById(credentials.getId()).join();

        assertThat(updated).contains(newCredentials);
        assertThat(retrieved).contains(newCredentials);

        final List<UserIdentifierDO> all = userIdentifiersRepository.getAll();
        assertThat(all)
                .doesNotContain(credentials.getIdentifiers().toArray(new UserIdentifierDO[0]));
        assertThat(all)
                .contains(newCredentials.getIdentifiers().toArray(new UserIdentifierDO[0]));
    }
}