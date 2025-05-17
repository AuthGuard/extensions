package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.ApiKeyDO;
import com.nexblocks.authguard.dal.persistence.ApiKeysRepository;
import io.smallrye.mutiny.Uni;

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

    @Inject
    public HibernateApiKeysRepository(final ReactiveQueryExecutor queryExecutor) {
        super(ApiKeyDO.class, queryExecutor);
    }

    @Override
    public Uni<Optional<ApiKeyDO>> getById(final long id) {
        return queryExecutor
                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, ApiKeyDO.class)
                        .setParameter(CommonFields.ID, id));
    }

    @Override
    public Uni<Collection<ApiKeyDO>> getByAppId(final long appId) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_APP_ID, ApiKeyDO.class)
                .setParameter(APP_ID_FIELD, appId))
                .map(Function.identity());
    }

    @Override
    public Uni<Optional<ApiKeyDO>> getByKey(final String key) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_KEY, ApiKeyDO.class)
                .setParameter(KEY_FIELD, key)).map(Function.identity());
    }
}
