package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.ClientDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.ClientsRepository;

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
    public CompletableFuture<List<ClientDO>> getAllForAccount(final String accountId) {
        return facade.find(Filters.eq("parentAccountId", accountId));
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByType(String clientType) {
        return facade.find(Filters.eq("clientType", clientType));
    }

    @Override
    public CompletableFuture<List<ClientDO>> getByDomain(String domain) {
        return facade.find(Filters.eq("domain", domain));
    }
}
