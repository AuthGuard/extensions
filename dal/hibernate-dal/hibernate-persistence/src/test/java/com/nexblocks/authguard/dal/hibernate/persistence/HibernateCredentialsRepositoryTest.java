package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.model.PasswordDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateCredentialsRepositoryTest {
    private HibernateCredentialsRepository repository;

    @BeforeAll
    void setup() {
        repository = new HibernateCredentialsRepository();
    }

    @Test
    void saveAndGetById() {
        final String id = UUID.randomUUID().toString();

        final CredentialsDO credentials = CredentialsDO.builder()
                .id(id)
                .accountId("account")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier("saveAndGetById")
                        .type(UserIdentifierDO.Type.USERNAME)
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
    void findByIdentifier() {
        final String id = UUID.randomUUID().toString();
        final String identifier = "findByIdentifier";

        final CredentialsDO credentials = CredentialsDO.builder()
                .id(id)
                .accountId("account")
                .identifiers(Collections.singleton(UserIdentifierDO.builder()
                        .identifier(identifier)
                        .type(UserIdentifierDO.Type.USERNAME)
                        .build()))
                .hashedPassword(PasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        final CredentialsDO persisted = repository.save(credentials).join();
        final Optional<CredentialsDO> retrieved = repository.findByIdentifier(identifier).join();

        assertThat(retrieved).contains(persisted);
    }
}