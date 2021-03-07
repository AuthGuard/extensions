package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.ApiKeyDO;
import com.nexblocks.authguard.dal.persistence.ApiKeysRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateApiKeysRepository extends AbstractHibernateRepository<ApiKeyDO>
        implements ApiKeysRepository {
    private static final String GET_BY_ID = "api_keys.getById";
    private static final String GET_BY_APP_ID = "api_keys.getByAppId";
    private static final String GET_BY_KEY = "api_keys.getByKey";

    private static final String APP_ID_FIELD = "appId";
    private static final String KEY_FIELD = "key";

    public HibernateApiKeysRepository() {
        super(ApiKeyDO.class);
    }

    @Override
    public CompletableFuture<Optional<ApiKeyDO>> getById(final String id) {
        return QueryExecutor
                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, ApiKeyDO.class)
                        .setParameter(CommonFields.ID, id))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<ApiKeyDO>> getByAppId(final String appId) {
        return QueryExecutor.getAList(session -> session.createNamedQuery(GET_BY_APP_ID, ApiKeyDO.class)
                .setParameter(APP_ID_FIELD, appId))
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Optional<ApiKeyDO>> getByKey(final String key) {
        return QueryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_KEY, ApiKeyDO.class)
                .setParameter(KEY_FIELD, key)).thenApply(Function.identity());
    }
}
