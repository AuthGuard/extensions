package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.CryptoKeyDO;
import com.nexblocks.authguard.dal.persistence.CryptoKeysRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateCryptoKeysRepository  extends AbstractHibernateRepository<CryptoKeyDO>
        implements CryptoKeysRepository {
    private static final String GET_BY_ID = "crypto_keys.getById";
    private static final String GET_BY_DOMAIN = "crypto_keys.getByDomain";
    private static final String GET_BY_ACCOUNT_ID = "crypto_keys.getByAccountId";
    private static final String GET_BY_APP_ID = "crypto_keys.getByAppId";

    @Inject
    public HibernateCryptoKeysRepository(final QueryExecutor queryExecutor) {
        super(CryptoKeyDO.class, queryExecutor);
    }

    @Override
    public CompletableFuture<Optional<CryptoKeyDO>> getById(final long id) {
        return queryExecutor
                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, CryptoKeyDO.class)
                        .setParameter(CommonFields.ID, id))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByDomain(final String domain, final Page<Instant> page) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_DOMAIN, CryptoKeyDO.class)
                        .setParameter(CommonFields.DOMAIN, domain)
                        .setParameter(CommonFields.CURSOR, page.getCursor()), page.getCount())
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByAccountId(final String domain, final long accountId,
                                                                final Page<Instant> page) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_ACCOUNT_ID, CryptoKeyDO.class)
                        .setParameter(CommonFields.DOMAIN, domain)
                        .setParameter(CommonFields.ACCOUNT_ID, accountId)
                        .setParameter(CommonFields.CURSOR, page.getCursor()), page.getCount())
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByAppId(final String domain, final long appId,
                                                            final Page<Instant> page) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_APP_ID, CryptoKeyDO.class)
                        .setParameter(CommonFields.DOMAIN, domain)
                        .setParameter(CommonFields.APP_ID, appId)
                        .setParameter(CommonFields.CURSOR, page.getCursor()), page.getCount())
                .thenApply(Function.identity());
    }
}
