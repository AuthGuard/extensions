package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AbstractDO;
import com.nexblocks.authguard.dal.model.TotpKeyDO;
import com.nexblocks.authguard.dal.persistence.TotpKeysRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockTotpKeysRepository extends AbstractRepository<TotpKeyDO>
        implements TotpKeysRepository {

    @Override
    public CompletableFuture<List<TotpKeyDO>> findByAccountId(final String domain, final long accountId) {
        return CompletableFuture.completedFuture(getRepo().values()
                .stream()
                .sorted(Comparator.comparing(AbstractDO::getCreatedAt).reversed())
                .filter(key -> key.getCreatedAt() != null
                        && Objects.equals(key.getDomain(), domain)
                        && Objects.equals(key.getAccountId(), accountId))
                .collect(Collectors.toList()));
    }
}
