package com.nexblocks.authguard.dal.memory.dal;

import com.nexblocks.authguard.dal.model.AbstractDO;
import com.nexblocks.authguard.dal.repository.Repository;
import io.smallrye.mutiny.Uni;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AbstractRepository<T extends AbstractDO> implements Repository<T> {
    private final Map<Long, T> repo;

    public AbstractRepository() {
        repo = new HashMap<>();
    }

    public Uni<T> save(final T record) {
        final Instant now = Instant.now();

        if (record.getCreatedAt() == null) {
            record.setCreatedAt(now);
        }

        record.setLastModified(now);

        repo.put(record.getId(), record);
        return Uni.createFrom().item(record);
    }

    public Uni<Optional<T>> getById(final long id) {
        return Uni.createFrom().item(Optional.ofNullable(repo.get(id)));
    }

    public Uni<Collection<T>> getAll() {
        return Uni.createFrom().item(repo.values());
    }

    public Uni<Optional<T>> update(final T record) {
        if (!repo.containsKey(record.getId())) {
            return Uni.createFrom().item(Optional.empty());
        }

        return override(record);
    }

    public Uni<Optional<T>> delete(final long id) {
        return Uni.createFrom().item(Optional.ofNullable(repo.remove(id)));
    }

    public Uni<Optional<T>> override(final T record) {
        repo.replace(record.getId(), record);

        return Uni.createFrom().item(Optional.of(record));
    }

    protected Map<Long, T> getRepo() {
        return repo;
    }
}
