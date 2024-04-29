package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AbstractDO;
import com.nexblocks.authguard.dal.model.EventDO;
import com.nexblocks.authguard.dal.persistence.EventsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockEventsRepository extends AbstractRepository<EventDO>
        implements EventsRepository {
    @Override
    public CompletableFuture<List<EventDO>> findByDomainDescending(final String domain,
                                                                   final Page<Instant> page) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .sorted(Comparator.comparing(AbstractDO::getCreatedAt).reversed())
                .filter(event -> event.getCreatedAt() != null
                        && event.getCreatedAt().isBefore(page.getCursor())
                        && event.getDomain().equals(domain))
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<EventDO>> findByDomainAndChannelDescending(final String domain,
                                                                             final String channel,
                                                                             final Page<Instant> page) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .sorted(Comparator.comparing(AbstractDO::getCreatedAt).reversed())
                .filter(event -> event.getCreatedAt() != null
                        && event.getCreatedAt().isBefore(page.getCursor())
                        && event.getDomain().equals(domain)
                        && event.getChannel().equals(channel))
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }
}
