package com.nexblocks.authguard.dal.hibernate.cache;

import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.SessionDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateSessionsRepositoryTest {
    private HibernateSessionsRepository repository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        repository = new HibernateSessionsRepository(new QueryExecutor(sessionProvider));
    }

    @Test
    public void getByToken() {
        final String id = UUID.randomUUID().toString();
        final String token = "getByToken-token";

        final SessionDO session = SessionDO.builder()
                .id(id)
                .sessionToken(token)
                .expiresAt(OffsetDateTime.now().withNano(0))
                .data(Collections.emptyMap())
                .build();

        final SessionDO persisted = repository.save(session).join();
        final Optional<SessionDO> retrieved = repository.getByToken(token).join();

        assertThat(retrieved).contains(persisted);
    }
}