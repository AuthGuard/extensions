package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.EventDO;
import com.nexblocks.authguard.dal.persistence.EventsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HibernateEventsRepository extends AbstractHibernateRepository<EventDO>
        implements EventsRepository {
    private static final String GET_BY_DOMAIN = "events.getByDomain";
    private static final String GET_BY_DOMAIN_AND_CHANNEL = "events.getByDomainAndChannel";

    private static final String DOMAIN_FIELD = "domain";
    private static final String CHANNEL_FIELD = "channel";
    private static final String CURSOR_FIELD = "cursor";

    @Inject
    protected HibernateEventsRepository(final ReactiveQueryExecutor queryExecutor) {
        super(EventDO.class, queryExecutor);
    }

    @Override
    public CompletableFuture<List<EventDO>> findByDomainDescending(final String domain, final Page<Instant> page) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_DOMAIN, EventDO.class)
                        .setParameter(DOMAIN_FIELD, domain)
                        .setParameter(CURSOR_FIELD, page.getCursor()), page.getCount())
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<List<EventDO>> findByDomainAndChannelDescending(final String domain,
                                                                             final String channel,
                                                                             final Page<Instant> page) {
        return queryExecutor
                .getAList(session -> session.createNamedQuery(GET_BY_DOMAIN_AND_CHANNEL, EventDO.class)
                        .setParameter(DOMAIN_FIELD, domain)
                        .setParameter(CHANNEL_FIELD, channel)
                        .setParameter(CURSOR_FIELD, page.getCursor()), page.getCount()
                ).subscribeAsCompletionStage();

    }
}
