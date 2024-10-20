package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AbstractDO;
import com.nexblocks.authguard.dal.model.CryptoKeyDO;
import com.nexblocks.authguard.dal.persistence.CryptoKeysRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockCryptoKeysRepository extends AbstractRepository<CryptoKeyDO>
        implements CryptoKeysRepository {
    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByDomain(final String domain, final Page<Instant> page) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .sorted(Comparator.comparing(AbstractDO::getCreatedAt).reversed())
                .filter(key -> key.getCreatedAt() != null
                        && key.getCreatedAt().isBefore(page.getCursor())
                        && Objects.equals(key.getDomain(), domain))
                .limit(page.getCount())
                .collect(Collectors.toList()));

    }

    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByAccountId(final String domain, final long accountId,
                                                                final Page<Instant> page) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .sorted(Comparator.comparing(AbstractDO::getCreatedAt).reversed())
                .filter(key -> key.getCreatedAt() != null
                        && key.getCreatedAt().isBefore(page.getCursor())
                        && Objects.equals(key.getDomain(), domain)
                        && Objects.equals(key.getAccountId(), accountId))
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<CryptoKeyDO>> findByAppId(final String domain, final long appId,
                                                            final Page<Instant> page) {
        return CompletableFuture.completedFuture(getRepo().values()
            .stream()
            .sorted(Comparator.comparing(AbstractDO::getCreatedAt).reversed())
            .filter(key -> key.getCreatedAt() != null
                    && key.getCreatedAt().isBefore(page.getCursor())
                    && Objects.equals(key.getDomain(), domain)
                    && Objects.equals(key.getAppId(), appId))
            .limit(page.getCount())
            .collect(Collectors.toList()));
    }
}
