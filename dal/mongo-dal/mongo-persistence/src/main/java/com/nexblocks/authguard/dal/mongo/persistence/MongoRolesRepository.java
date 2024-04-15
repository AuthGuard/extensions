package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.RoleDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.Page;
import com.nexblocks.authguard.dal.persistence.RolesRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MongoRolesRepository extends AbstractMongoRepository<RoleDO> implements RolesRepository {
    private static final String COLLECTION_KEY = "roles";

    @Inject
    public MongoRolesRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.ROLES, RoleDO.class);
    }

    @Override
    public CompletableFuture<Collection<RoleDO>> getAll(final String domain, final Page page) {
        return facade.find(Filters.and(
                        Filters.eq("domain", domain),
                        Filters.gt("_id", page.getCursor())
                ), page.getCount())
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Optional<RoleDO>> getByName(final String name, final String domain) {
        return facade.findOne(Filters.and(
                Filters.eq("name", name),
                Filters.eq("domain", domain)
        ));
    }

    @Override
    public CompletableFuture<Collection<RoleDO>> getMultiple(final Collection<String> roles, final String domain) {
        return facade.find(Filters.and(
                Filters.in("name", roles),
                Filters.eq("domain", domain)
        )).thenApply(Function.identity());
    }
}
