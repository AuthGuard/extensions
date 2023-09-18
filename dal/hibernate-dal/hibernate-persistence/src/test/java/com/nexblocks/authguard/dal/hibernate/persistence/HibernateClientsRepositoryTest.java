package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.ClientDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateClientsRepositoryTest {

    private HibernateClientsRepository repository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateClientsRepository(new QueryExecutor(sessionProvider));
    }

    @Test
    public void saveAndGetById() {
        final String id = UUID.randomUUID().toString();

        final ClientDO client = ClientDO.builder()
                .id(id)
                .clientType("NONE")
                .build();

        final ClientDO persisted = repository.save(client).join();
        final Optional<ClientDO> retrieved = repository.getById(id).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void getByExternalId() {
        final String id = UUID.randomUUID().toString();
        final String externalId = "getByExternalId";

        final ClientDO client = ClientDO.builder()
                .id(id)
                .externalId(externalId)
                .clientType("NONE")
                .build();

        final ClientDO persisted = repository.save(client).join();
        final Optional<ClientDO> retrieved = repository.getByExternalId(externalId).join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void getAllForAccount() {
        final String id = UUID.randomUUID().toString();
        final String accountId = "account";

        final ClientDO client = ClientDO.builder()
                .id(id)
                .accountId(accountId)
                .clientType("NONE")
                .build();

        final ClientDO persisted = repository.save(client).join();
        final List<ClientDO> retrieved = repository.getAllForAccount(accountId).join();

        assertThat(retrieved).containsOnly(persisted);
    }

    @Test
    void getByType() {
        final String id = UUID.randomUUID().toString();

        final ClientDO client = ClientDO.builder()
                .id(id)
                .clientType("AUTH")
                .build();

        final ClientDO persisted = repository.save(client).join();
        final List<ClientDO> retrieved = repository.getByType("AUTH").join();

        assertThat(retrieved).containsOnly(persisted);
    }

    @Test
    void getByDomain() {
        final String id = UUID.randomUUID().toString();

        final ClientDO client = ClientDO.builder()
                .id(id)
                .domain("test")
                .build();

        final ClientDO persisted = repository.save(client).join();
        final List<ClientDO> retrieved = repository.getByDomain("test").join();

        assertThat(retrieved).containsOnly(persisted);
    }
}