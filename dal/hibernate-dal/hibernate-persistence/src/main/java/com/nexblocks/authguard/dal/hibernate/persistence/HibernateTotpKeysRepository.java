package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.TotpKeyDO;
import com.nexblocks.authguard.dal.persistence.TotpKeysRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateTotpKeysRepository extends AbstractHibernateRepository<TotpKeyDO>
        implements TotpKeysRepository {
    private static final String GET_BY_ACCOUNT_ID = "totp_keys.getByAccountId";

    @Inject
    public HibernateTotpKeysRepository(final ReactiveQueryExecutor queryExecutor) {
        super(TotpKeyDO.class, queryExecutor);
    }

    @Override
    public CompletableFuture<List<TotpKeyDO>> findByAccountId(final String domain, final long accountId) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ACCOUNT_ID, TotpKeyDO.class)
                        .setParameter(CommonFields.DOMAIN, domain)
                        .setParameter(CommonFields.ACCOUNT_ID, accountId))
                .subscribeAsCompletionStage()
                .thenApply(Function.identity());
    }
}
