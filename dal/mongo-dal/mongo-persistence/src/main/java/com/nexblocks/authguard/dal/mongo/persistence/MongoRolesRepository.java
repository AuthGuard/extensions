package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.RoleDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
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
    public CompletableFuture<Collection<RoleDO>> getAll() {
        return facade.findAll()
                .thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<Optional<RoleDO>> getByName(final String name) {
        return facade.findOne(Filters.eq("name", name));
    }

    @Override
    public CompletableFuture<Collection<RoleDO>> getMultiple(final Collection<String> roles) {
        return facade.find(Filters.in("name", roles))
                .thenApply(Function.identity());
    }
}
