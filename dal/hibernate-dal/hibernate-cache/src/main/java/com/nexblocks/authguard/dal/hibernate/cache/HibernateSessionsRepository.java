package com.nexblocks.authguard.dal.hibernate.cache;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.cache.SessionsRepository;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.SessionDO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HibernateSessionsRepository extends AbstractHibernateRepository<SessionDO>
        implements SessionsRepository {
    private static final String GET_BY_TOKEN = "sessions.getByToken";
    private static final String GET_BY_ACCOUNT_ID = "getByAccountId";
    private static final String TOKEN_FIELD = "token";

    @Inject
    public HibernateSessionsRepository(final ReactiveQueryExecutor queryExecutor) {
        super(SessionDO.class, queryExecutor);
    }

    @Override
    public CompletableFuture<Optional<SessionDO>> getByToken(final String token) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_TOKEN, SessionDO.class)
                .setParameter(TOKEN_FIELD, token))
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Optional<SessionDO>> deleteByToken(final String sessionToken) {
        return getByToken(sessionToken)
                .thenCompose(opt -> {
                    if (opt.isPresent()) {
                        return delete(opt.get().getId()).subscribeAsCompletionStage();
                    }

                    return CompletableFuture.completedFuture(Optional.empty());
                });
    }

    @Override
    public CompletableFuture<List<SessionDO>> findByAccountId(final long accountId, final String domain) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_ACCOUNT_ID, SessionDO.class)
                .setParameter(CommonFields.DOMAIN, domain)
                .setParameter(CommonFields.ACCOUNT_ID, accountId))
                .subscribeAsCompletionStage();

    }
}
