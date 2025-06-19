package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.AppDO;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.persistence.ApplicationsRepository;
import com.nexblocks.authguard.dal.persistence.Page;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Optional;

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
    public Uni<Optional<AppDO>> getByExternalId(final String externalId) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_EXTERNAL_ID, AppDO.class)
                .setParameter(EXTERNAL_ID_FIELD, externalId));
    }

    @Override
    public Uni<List<AppDO>> getAllForAccount(final long accountId, final Page<Long> page) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_ACCOUNT_ID, AppDO.class)
                .setParameter(ACCOUNT_ID_FIELD, accountId)
                .setParameter(CURSOR_FIELD, page.getCursor()), page.getCount());
    }

    @Override
    public Uni<AppDO> addAppPermissions(final AppDO app, final List<PermissionDO> permissions) {
        String sql = "INSERT INTO apps_permissions (AppDO_id, permissions_id) " +
                "VALUES (:appId, :permissionId)";

        return queryExecutor.getSessionFactory().withTransaction(session -> {
            List<Uni<Void>> insertUnis = permissions.stream()
                    .map(permission -> session.createNativeQuery(sql)
                            .setParameter("appId", app.getId())
                            .setParameter("permissionId", permission.getId())
                            .executeUpdate()
                            .replaceWithVoid()
                    )
                    .toList();

            return Uni.combine().all().unis(insertUnis)
                    .with(list -> app);
        });

    }

    @Override
    public Uni<AppDO> removeAppPermissions(final AppDO app, final List<PermissionDO> permissions) {
        String sql = "DELETE FROM apps_permissions " +
                "WHERE AppDO_id = :appId and permissions_id in :permissionIds";

        return queryExecutor.getSessionFactory().withTransaction(session -> session.createNativeQuery(sql)
                .setParameter("appId", app.getId())
                .setParameter("permissionIds", permissions.stream().map(PermissionDO::getId).toList())
                .executeUpdate()
                .map(ignored -> app));
    }
}
