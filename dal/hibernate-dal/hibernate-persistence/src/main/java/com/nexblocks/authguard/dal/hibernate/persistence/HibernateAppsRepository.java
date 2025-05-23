package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.AppDO;
import com.nexblocks.authguard.dal.persistence.ApplicationsRepository;
import com.nexblocks.authguard.dal.persistence.Page;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateAppsRepository extends AbstractHibernateRepository<AppDO>
        implements ApplicationsRepository {
    private static final String GET_BY_ID = "apps.getById";
    private static final String GET_BY_EXTERNAL_ID = "apps.getByExternalId";
    private static final String GET_BY_ACCOUNT_ID = "apps.getByAccountId";

    private static final String EXTERNAL_ID_FIELD = "externalId";
    private static final String ACCOUNT_ID_FIELD = "parentAccountId";
    private static final String CURSOR_FIELD = "cursor";

    @Inject
    public HibernateAppsRepository(final ReactiveQueryExecutor queryExecutor) {
        super(AppDO.class, queryExecutor);
    }

    @Override
    public Uni<Optional<AppDO>> getById(final long id) {
        return queryExecutor
                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, AppDO.class)
                        .setParameter(CommonFields.ID, id));
    }

    @Override
    public CompletableFuture<Optional<AppDO>> getByExternalId(final String externalId) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_EXTERNAL_ID, AppDO.class)
                .setParameter(EXTERNAL_ID_FIELD, externalId))
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<List<AppDO>> getAllForAccount(final long accountId, final Page<Long> page) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_ACCOUNT_ID, AppDO.class)
                .setParameter(ACCOUNT_ID_FIELD, accountId)
                .setParameter(CURSOR_FIELD, page.getCursor()), page.getCount())
                .subscribeAsCompletionStage();
    }
}
