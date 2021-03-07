package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.persistence.CredentialsRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateCredentialsRepository extends AbstractHibernateRepository<CredentialsDO>
        implements CredentialsRepository {
    private static final String GET_BY_ID = "credentials.getById";
    private static final String GET_BY_IDENTIFIER = "credentials.getByIdentifier";
    private static final String IDENTIFIER_FIELD = "identifier";

    public HibernateCredentialsRepository() {
        super(CredentialsDO.class);
    }

    @Override
    public CompletableFuture<Optional<CredentialsDO>> getById(final String id) {
        return QueryExecutor
                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, CredentialsDO.class)
                        .setParameter(CommonFields.ID, id))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Optional<CredentialsDO>> findByIdentifier(final String identifier) {
        return QueryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_IDENTIFIER, CredentialsDO.class)
                .setParameter(IDENTIFIER_FIELD, identifier));
    }
}
