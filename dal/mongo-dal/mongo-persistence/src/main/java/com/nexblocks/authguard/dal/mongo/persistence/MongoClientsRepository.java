package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.ClientDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.ClientsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoClientsRepository extends AbstractMongoRepository<ClientDO> implements ClientsRepository {
    private static final String COLLECTION_KEY = "clients";

    @Inject
    public MongoClientsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.CLIENTS, ClientDO.class);
    }

    @Override
    public CompletableFuture<Optional<ClientDO>> getByExternalId(final String externalId) {
        return facade.findOne(Filters.eq("externalId", externalId));
    }

    @Override
    public CompletableFuture<List<ClientDO>> getAllForAccount(final long accountId, final Page<Long> page) {
        return facade.find(Filters.and(
                Filters.eq("parentAccountId", accountId),
                Filters.gt("_id", page.getCursor())
        ), page.getCount());
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByType(final String clientType, final Page<Long> page) {
        return facade.find(Filters.and(
                Filters.eq("clientType", clientType),
                Filters.gt("_id", page.getCursor())
        ), page.getCount());
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByDomain(final String domain, final Page<Long> page) {
        return facade.find(Filters.and(
                Filters.eq("domain", domain),
                Filters.gt("_id", page.getCursor())
        ), page.getCount());
    }
}
