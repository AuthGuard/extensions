package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.EmailDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateAccountsRepositoryTest {
    private HibernateAccountsRepository repository;

    @BeforeAll
    void setup() {
        repository = new HibernateAccountsRepository();
    }

    @Test
    void saveAndGetById() {
        final String id = UUID.randomUUID().toString();
        final EmailDO email = EmailDO.builder()
                .email("saveAndGetById@test.com")
                .build();

        final AccountDO account = AccountDO.builder()
                .id(id)
                .email(email)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.getById(id).join();

//        List<AccountDO> results = QueryExecutor.getAList(session ->
//                session.createQuery("SELECT account FROM AccountDO account " +
//                        "LEFT JOIN FETCH account.permissions " +
//                        "LEFT JOIN FETCH account.roles", AccountDO.class)).join();
//
//        System.out.println(results);

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getByExternalId() {
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
                .build();

        final AccountDO persisted = repository.save(account).join();
        final Optional<AccountDO> retrieved = repository.getByExternalId(externalId).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getByRole() {
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
                .build();

        final AccountDO persisted = repository.save(account).join();
        final List<AccountDO> retrieved = repository.getByRole(role).join();

        assertThat(retrieved).containsOnly(persisted);
    }
}