package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.Page;
import com.nexblocks.authguard.dal.persistence.PermissionsRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MongoPermissionsRepository extends AbstractMongoRepository<PermissionDO> implements PermissionsRepository {
    private static final String COLLECTION_KEY = "permissions";

    @Inject
    public MongoPermissionsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.PERMISSIONS, PermissionDO.class);
    }

    @Override
    public CompletableFuture<Optional<PermissionDO>> search(final String group, final String name, final String domain) {
        return facade.findOne(Filters.and(
                Filters.eq("group", group),
                Filters.eq("name", name),
                Filters.eq("domain", domain)
        )).subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Collection<PermissionDO>> getAll(final String domain, final Page<Long> page) {
        return facade.find(Filters.and(
                        Filters.eq("domain", domain),
                        Filters.gt("_id", page.getCursor())
                ), page.getCount())
                .subscribeAsCompletionStage()
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Collection<PermissionDO>> getAllForGroup(final String group,
                                                                      final String domain,
                                                                      final Page<Long> page) {
        return facade.find(Filters.and(
                Filters.eq("group", group),
                Filters.eq("domain", domain),
                Filters.gt("_id", page.getCursor())
        ), page.getCount()).subscribeAsCompletionStage().thenApply(Function.identity());
    }
}
