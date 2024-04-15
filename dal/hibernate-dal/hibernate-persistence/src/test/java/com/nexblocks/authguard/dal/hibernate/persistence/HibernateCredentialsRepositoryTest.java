package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.model.PasswordDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
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
public class HibernateCredentialsRepositoryTest {
    private HibernateCredentialsRepository repository;
    private HibernateUserIdentifiersRepository userIdentifiersRepository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        final QueryExecutor queryExecutor = new QueryExecutor(sessionProvider);

        userIdentifiersRepository = new HibernateUserIdentifiersRepository(queryExecutor);
        repository = new HibernateCredentialsRepository(queryExecutor);
    }

    @Test
    public void saveAndGetById() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        final CredentialsDO credentials = CredentialsDO.builder()
                .id(id)
                .accountId(101)
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveAndGetById")
                        .type(UserIdentifierDO.Type.USERNAME)
                        .domain("main")
                        .build()))
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        final CredentialsDO persisted = repository.save(credentials).join();
        final Optional<CredentialsDO> retrieved = repository.getById(id).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void findByIdentifier() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        final String identifier = "findByIdentifier";

        final CredentialsDO credentials = CredentialsDO.builder()
                .id(id)
                .accountId(101)
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

        final CredentialsDO persisted = repository.save(credentials).join();
        final Optional<CredentialsDO> retrieved = repository.findByIdentifier(identifier, "main").join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void updatePassword() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        final String identifier = "updatePassword";

        final CredentialsDO credentials = CredentialsDO.builder()
                .id(id)
                .accountId(101)
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .type(UserIdentifierDO.Type.USERNAME)
                        .build()))
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        final CredentialsDO newCredentials = CredentialsDO.builder()
                .id(credentials.getId())
                .accountId(101)
                .identifiers(credentials.getIdentifiers())
                .hashedPassword(PasswordDO.builder()
                        .password("new_password")
                        .salt("salt")
                        .build())
                .build();

        final CredentialsDO persisted = repository.save(credentials).join();

        final Optional<CredentialsDO> updated = repository.update(newCredentials).join();
        final Optional<CredentialsDO> retrieved = repository.getById(credentials.getId()).join();

        assertThat(updated).contains(newCredentials);
        assertThat(retrieved).contains(newCredentials);
    }

    @Test
    public void removeIdentifier() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        final String identifier = "removeIdentifier";

        final CredentialsDO credentials = CredentialsDO.builder()
                .id(id)
                .accountId(101)
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .type(UserIdentifierDO.Type.USERNAME)
                        .build()))
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        final CredentialsDO newCredentials = CredentialsDO.builder()
                .id(id)
                .accountId(101)
                .identifiers(Collections.emptySet())
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        repository.save(credentials).join();

        final Optional<CredentialsDO> updated = repository.update(newCredentials).join();
        final Optional<CredentialsDO> retrieved = repository.getById(credentials.getId()).join();

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
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        final String identifier = "updateIdentifier";
        final String newIdentifier = "updateIdentifierNew";

        final CredentialsDO credentials = CredentialsDO.builder()
                .id(id)
                .accountId(101)
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

        final CredentialsDO beforeUpdate = repository.getById(credentials.getId())
                .join()
                .get();

        final CredentialsDO newCredentials = CredentialsDO.builder()
                .id(beforeUpdate.getId())
                .accountId(beforeUpdate.getAccountId())
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

        final Optional<CredentialsDO> updated = repository.update(newCredentials).join();
        final Optional<CredentialsDO> retrieved = repository.getById(credentials.getId()).join();

        assertThat(updated).contains(newCredentials);
        assertThat(retrieved).contains(newCredentials);

        final List<UserIdentifierDO> all = userIdentifiersRepository.getAll();
        assertThat(all)
                .doesNotContain(credentials.getIdentifiers().toArray(new UserIdentifierDO[0]));
        assertThat(all)
                .contains(newCredentials.getIdentifiers().toArray(new UserIdentifierDO[0]));
    }
}