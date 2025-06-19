package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.AppDO;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.ApplicationsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.nexblocks.authguard.service.exceptions.ServiceNotFoundException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;
import io.smallrye.mutiny.Uni;

public class MongoApplicationsRepository extends AbstractMongoRepository<AppDO> implements ApplicationsRepository {
    private static final String COLLECTION_KEY = "apps";

    @Inject
    public MongoApplicationsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.APPLICATIONS, AppDO.class);
    }

    @Override
    public Uni<Optional<AppDO>> getByExternalId(final String externalId) {
        return facade.findOne(Filters.eq("externalId", externalId));
    }

    @Override
    public Uni<List<AppDO>> getAllForAccount(final long accountId, final Page<Long> page) {
        return facade.find(Filters.and(
                Filters.eq("parentAccountId", accountId),
                Filters.gt("_id", page.getCursor())
        ), page.getCount());
    }

    @Override
    public Uni<AppDO> addAppPermissions(final AppDO app, final List<PermissionDO> permissions) {
        return getById(app.getId())
                .map(opt -> opt
                        .map(retrieved -> {
                            Set<PermissionDO> combinedPermissions = Stream.concat(app.getPermissions().stream(), permissions.stream())
                                    .collect(Collectors.toSet());

                            retrieved.setPermissions(combinedPermissions);

                            return retrieved;
                        })
                        .orElseThrow(() -> new ServiceNotFoundException(ErrorCode.APP_DOES_NOT_EXIST, "App does not exist")))
                .flatMap(updated -> facade.replaceById(app.getId(), updated))
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.APP_DOES_NOT_EXIST, "App does not exist")));

    }

    @Override
    public Uni<AppDO> removeAppPermissions(final AppDO app, final List<PermissionDO> permissions) {
        return getById(app.getId())
                .map(opt -> opt
                        .map(retrieved -> {
                            Set<String> permissionsFullNames = permissions.stream()
                                    .map(permission -> permission.getPermissionGroup() + ":" + permission.getName())
                                    .collect(Collectors.toSet());

                            Set<PermissionDO> filteredPermissions = retrieved.getPermissions().stream()
                                    .filter(permission -> !permissionsFullNames.contains(permission.getPermissionGroup() + ":" + permission.getName()))
                                    .collect(Collectors.toSet());

                            retrieved.setPermissions(filteredPermissions);

                            return retrieved;
                        })
                        .orElseThrow(() -> new ServiceNotFoundException(ErrorCode.APP_DOES_NOT_EXIST, "App does not exist")))
                .flatMap(updated -> facade.replaceById(app.getId(), updated))
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.APP_DOES_NOT_EXIST, "App does not exist")));

    }
}
