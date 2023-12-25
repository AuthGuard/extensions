package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
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
public class HibernateAppsRepositoryTest {
    private HibernateAppsRepository repository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateAppsRepository(new QueryExecutor(sessionProvider));
    }

    @Test
    public void saveAndGetById() {
        final long id = UUID.randomUUID().getMostSignificantBits();

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
    public void getByExternalId() {
        final long id = UUID.randomUUID().getMostSignificantBits();
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
    public void getAllForAccount() {
        final long id = UUID.randomUUID().getMostSignificantBits();

        final AppDO app = AppDO.builder()
                .id(id)
                .parentAccountId(101L)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AppDO persisted = repository.save(app).join();
        final List<AppDO> retrieved = repository.getAllForAccount(101).join();

        assertThat(retrieved).containsOnly(persisted);
    }
}