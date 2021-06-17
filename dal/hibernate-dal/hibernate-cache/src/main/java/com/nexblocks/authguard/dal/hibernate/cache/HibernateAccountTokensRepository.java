package com.nexblocks.authguard.dal.hibernate.cache;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.cache.AccountTokensRepository;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.AccountTokenDO;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HibernateAccountTokensRepository extends AbstractHibernateRepository<AccountTokenDO>
        implements AccountTokensRepository {
    private static final String GET_BY_TOKEN = "account_tokens.getByToken";

    private static final String TOKEN_FIELD = "token";

    @Inject
    public HibernateAccountTokensRepository(final QueryExecutor queryExecutor) {
        super(AccountTokenDO.class, queryExecutor);
    }

    @Override
    public CompletableFuture<Optional<AccountTokenDO>> getByToken(final String token) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_TOKEN, AccountTokenDO.class)
                .setParameter(TOKEN_FIELD, token));
    }

    @Override
    public CompletableFuture<Optional<AccountTokenDO>> deleteToken(final String token) {
        return getByToken(token)
                .thenCompose(opt -> {
                    if (opt.isPresent()) {
                        return delete(opt.get().getId());
                    }

                    return CompletableFuture.completedFuture(Optional.empty());
                });
    }
}
