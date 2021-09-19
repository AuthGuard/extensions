package com.nexblocks.authguard.dal.memory.dal;

import com.nexblocks.authguard.dal.model.AbstractDO;
import com.nexblocks.authguard.dal.repository.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AbstractRepository<T extends AbstractDO> implements Repository<T> {
    private final Map<String, T> repo;

    public AbstractRepository() {
        repo = new HashMap<>();
    }

    public CompletableFuture<T> save(final T record) {
        final OffsetDateTime now = OffsetDateTime.now();

        if (record.getCreatedAt() == null) {
            record.setCreatedAt(now);
        }

        record.setLastModified(now);

        repo.put(record.getId(), record);
        return CompletableFuture.completedFuture(record);
    }

    public CompletableFuture<Optional<T>> getById(final String id) {
        return CompletableFuture.completedFuture(Optional.ofNullable(repo.get(id)));
    }

    public CompletableFuture<Collection<T>> getAll() {
        return CompletableFuture.completedFuture(repo.values());
    }

    public CompletableFuture<Optional<T>> update(final T record) {
        if (!repo.containsKey(record.getId())) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return override(record);
    }

    public CompletableFuture<Optional<T>> delete(final String id) {
        return CompletableFuture.completedFuture(Optional.ofNullable(repo.remove(id)));
    }

    public CompletableFuture<Optional<T>> override(final T record) {
        repo.replace(record.getId(), record);

        return CompletableFuture.completedFuture(Optional.of(record));
    }

    protected Map<String, T> getRepo() {
        return repo;
    }
}
