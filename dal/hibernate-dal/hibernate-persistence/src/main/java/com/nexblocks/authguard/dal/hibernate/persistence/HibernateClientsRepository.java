package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.ClientDO;
import com.nexblocks.authguard.dal.persistence.ClientsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateClientsRepository extends AbstractHibernateRepository<ClientDO>
        implements ClientsRepository {
    private static final String GET_BY_ID = "clients.getById";
    private static final String GET_BY_EXTERNAL_ID = "clients.getByExternalId";
    private static final String GET_BY_ACCOUNT_ID = "clients.getByAccountId";
    private static final String GET_BY_DOMAIN = "clients.getByDomain";
    private static final String GET_BY_CLIENT_TYPE = "clients.getByClientType";

    private static final String EXTERNAL_ID_FIELD = "externalId";
    private static final String ACCOUNT_ID_FIELD = "parentAccountId";
    private static final String DOMAIN_FIELD = "domain";
    private static final String CLIENT_TYPE_FIELD = "clientType";
    private static final String CURSOR_FIELD = "cursor";

    @Inject
    public HibernateClientsRepository(final QueryExecutor queryExecutor) {
        super(ClientDO.class, queryExecutor);
    }

    @Override
    public CompletableFuture<Optional<ClientDO>> getById(final long id) {
        return queryExecutor
                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, ClientDO.class)
                        .setParameter(CommonFields.ID, id))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Optional<ClientDO>> getByExternalId(final String externalId) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_EXTERNAL_ID, ClientDO.class)
                .setParameter(EXTERNAL_ID_FIELD, externalId));
    }

    @Override
    public CompletableFuture<List<ClientDO>> getAllForAccount(final long accountId, final Page page) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_ACCOUNT_ID, ClientDO.class)
                .setParameter(ACCOUNT_ID_FIELD, accountId)
                .setParameter(CURSOR_FIELD,  page.getCursor()), page.getCount());
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByType(final String clientType, final Page page) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_CLIENT_TYPE, ClientDO.class)
                .setParameter(CLIENT_TYPE_FIELD, clientType)
                .setParameter(CURSOR_FIELD,  page.getCursor()), page.getCount());
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByDomain(final String domain, final Page page) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_DOMAIN, ClientDO.class)
                .setParameter(DOMAIN_FIELD, domain)
                .setParameter(CURSOR_FIELD,  page.getCursor()), page.getCount());
    }
}
