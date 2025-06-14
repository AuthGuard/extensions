package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.Page;
import com.nexblocks.authguard.dal.persistence.PermissionsRepository;

import java.util.Collection;
import java.util.Optional;

import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;
import io.smallrye.mutiny.Uni;
import java.util.function.Function;

public class MongoPermissionsRepository extends AbstractMongoRepository<PermissionDO> implements PermissionsRepository {
    private static final String COLLECTION_KEY = "permissions";

    @Inject
    public MongoPermissionsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.PERMISSIONS, PermissionDO.class);
    }

    @Override
    public Uni<PermissionDO> save(final PermissionDO record) {
        return super.save(record)
                .onFailure()
                .transform(throwable -> {
                    if (throwable instanceof MongoWriteException) {
                        return mapWriteErrors((MongoWriteException) throwable);
                    }

                    return throwable;
                });
    }

    @Override
    public Uni<Optional<PermissionDO>> search(final String group, final String name, final String domain) {
        return facade.findOne(Filters.and(
                Filters.eq("permissionGroup", group),
                Filters.eq("name", name),
                Filters.eq("domain", domain)
        ));
    }

    @Override
    public Uni<Collection<PermissionDO>> getAll(final String domain, final Page<Long> page) {
        return facade.find(Filters.and(
                        Filters.eq("domain", domain),
                        Filters.gt("_id", page.getCursor())
                ), page.getCount())
                .map(Function.identity());
    }

    @Override
    public Uni<Collection<PermissionDO>> getAllForGroup(final String group,
                                                                      final String domain,
                                                                      final Page<Long> page) {
        return facade.find(Filters.and(
                Filters.eq("permissionGroup", group),
                Filters.eq("domain", domain),
                Filters.gt("_id", page.getCursor())
        ), page.getCount()).map(Function.identity());
    }

    private RuntimeException mapWriteErrors(final MongoWriteException e) {
        if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
            return new ServiceConflictException(ErrorCode.PERMISSION_ALREADY_EXIST, "Permission already exists");
        }

        return e;
    }
}
