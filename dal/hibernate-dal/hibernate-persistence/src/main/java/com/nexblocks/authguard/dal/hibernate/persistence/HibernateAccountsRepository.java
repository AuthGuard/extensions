package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HibernateAccountsRepository extends AbstractHibernateRepository<AccountDO>
        implements AccountsRepository {
    private static final String GET_BY_ID = "accounts.getById";
    private static final String GET_BY_EXTERNAL_ID = "accounts.getByExternalId";
    private static final String GET_BY_ROLE = "accounts.getByRole";

    private static final String ID_FIELD = "id";
    private static final String EXTERNAL_ID_FIELD = "externalId";
    private static final String ROLE_FIELD = "role";

    public HibernateAccountsRepository() {
        super(AccountDO.class);
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getById(final String id) {
        return QueryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_ID, AccountDO.class)
                .setParameter(ID_FIELD, id));
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByExternalId(final String externalId) {
        return QueryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_EXTERNAL_ID, AccountDO.class)
                .setParameter(EXTERNAL_ID_FIELD, externalId));
    }

    @Override
    public CompletableFuture<List<AccountDO>> getByRole(final String role) {
        return QueryExecutor.getAList(session -> session.createNamedQuery(GET_BY_ROLE, AccountDO.class)
                .setParameter(ROLE_FIELD, role));
    }
}
