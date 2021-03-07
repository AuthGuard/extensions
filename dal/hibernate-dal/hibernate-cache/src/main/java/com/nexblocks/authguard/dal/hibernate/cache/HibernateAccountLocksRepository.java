package com.nexblocks.authguard.dal.hibernate.cache;

import com.nexblocks.authguard.dal.cache.AccountLocksRepository;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.AccountLockDO;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateAccountLocksRepository extends AbstractHibernateRepository<AccountLockDO>
        implements AccountLocksRepository {

    private static final String GET_BY_ACCOUNT_ID = "account_locks.getByAccountId";

    private static final String ACCOUNT_ID_FIELD = "token";

    public HibernateAccountLocksRepository() {
        super(AccountLockDO.class);
    }

    @Override
    public CompletableFuture<Collection<AccountLockDO>> findByAccountId(final String accountId) {
        return QueryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ACCOUNT_ID, AccountLockDO.class)
                        .setParameter(ACCOUNT_ID_FIELD, accountId))
                .thenApply(Function.identity());
    }
}
