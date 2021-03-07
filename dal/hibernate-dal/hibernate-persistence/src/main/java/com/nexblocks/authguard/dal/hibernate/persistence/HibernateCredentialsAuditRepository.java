package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.CredentialsAuditDO;
import com.nexblocks.authguard.dal.persistence.CredentialsAuditRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HibernateCredentialsAuditRepository extends AbstractHibernateRepository<CredentialsAuditDO>
        implements CredentialsAuditRepository {
    private static final String GET_BY_CREDENTIALS_ID = "credentials_audit.getByCredentialsId";

    private static final String CREDENTIALS_ID_FIELD = "credentialsId";

    public HibernateCredentialsAuditRepository() {
        super(CredentialsAuditDO.class);
    }

    @Override
    public CompletableFuture<List<CredentialsAuditDO>> findByCredentialsId(final String credentialsId) {
        return QueryExecutor.getAList(session -> session.createNamedQuery(GET_BY_CREDENTIALS_ID, CredentialsAuditDO.class)
                .setParameter(CREDENTIALS_ID_FIELD, credentialsId));
    }
}
