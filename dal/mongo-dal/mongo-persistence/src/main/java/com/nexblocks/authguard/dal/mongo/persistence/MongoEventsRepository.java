package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.nexblocks.authguard.dal.model.EventDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.EventsRepository;
import com.nexblocks.authguard.dal.persistence.Page;
import org.bson.conversions.Bson;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MongoEventsRepository extends AbstractMongoRepository<EventDO>
        implements EventsRepository {
    private static final String COLLECTION_KEY = "events";

    @Inject
    public MongoEventsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.EXCHANGE_ATTEMPTS,
                EventDO.class);
    }

    @Override
    public CompletableFuture<List<EventDO>> findByDomainDescending(final String domain, final Page<Instant> page) {
        return facade.find(Filters.and(
                Filters.eq("domain", domain),
                Filters.gte("createdAt", page.getCursor())
        ), page.getCount()).thenApply(Function.identity());
    }

    @Override
    public CompletableFuture<List<EventDO>> findByDomainAndChannelDescending(final String domain,
                                                                             final String channel,
                                                                             final Page<Instant> page) {
        Bson sort = Sorts.descending("createdAt");

        return facade.find(Filters.and(
                Filters.eq("domain", domain),
                Filters.eq("channel", channel),
                Filters.lte("createdAt", page.getCursor())
        ), sort, page.getCount()).thenApply(Function.identity());
    }
}
