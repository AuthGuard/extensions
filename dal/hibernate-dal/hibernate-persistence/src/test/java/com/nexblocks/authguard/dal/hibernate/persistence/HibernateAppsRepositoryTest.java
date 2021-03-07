package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.model.AppDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateAppsRepositoryTest {
    private HibernateAppsRepository repository;

    @BeforeAll
    void setup() {
        repository = new HibernateAppsRepository();
    }

    @Test
    void saveAndGetById() {
        final String id = UUID.randomUUID().toString();

        final AppDO app = AppDO.builder()
                .id(id)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AppDO persisted = repository.save(app).join();
        final Optional<AppDO> retrieved = repository.getById(id).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getByExternalId() {
        final String id = UUID.randomUUID().toString();
        final String externalId = "getByExternalId";

        final AppDO app = AppDO.builder()
                .id(id)
                .externalId(externalId)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AppDO persisted = repository.save(app).join();
        final Optional<AppDO> retrieved = repository.getByExternalId(externalId).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    void getAllForAccount() {
        final String id = UUID.randomUUID().toString();
        final String accountId = "account";

        final AppDO app = AppDO.builder()
                .id(id)
                .parentAccountId(accountId)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AppDO persisted = repository.save(app).join();
        final List<AppDO> retrieved = repository.getAllForAccount(accountId).join();

        assertThat(retrieved).containsOnly(persisted);
    }
}