package com.nexblocks.authguard.dal.hibernate;

import com.nexblocks.authguard.dal.hibernate.cache.HibernateSessionsRepository;
import com.nexblocks.authguard.dal.model.SessionDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateSessionsRepositoryTest {
    private HibernateSessionsRepository repository;

    @BeforeAll
    public void setup() {
        repository = new HibernateSessionsRepository();
    }

    @Test
    public void getByToken() {
        final String id = UUID.randomUUID().toString();
        final String token = "getByToken-token";

        final SessionDO session = SessionDO.builder()
                .id(id)
                .sessionToken(token)
                .expiresAt(ZonedDateTime.now())
                .data(Collections.emptyMap())
                .build();

        final SessionDO persisted = repository.save(session).join();
        final Optional<SessionDO> retrieved = repository.getByToken(token).join();

        assertThat(retrieved).contains(persisted);
    }
}