package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.IdempotentRecordDO;
import com.nexblocks.authguard.dal.persistence.IdempotentRecordsRepository;

import java.util.List;
import java.util.Optional;
import io.smallrye.mutiny.Uni;
import java.util.stream.Collectors;

@Singleton
public class MockIdempotentRecordsRepository extends AbstractRepository<IdempotentRecordDO> implements IdempotentRecordsRepository {
    @Override
    public Uni<List<IdempotentRecordDO>> findByKey(final String idempotentKey) {
        return Uni.createFrom().item(getRepo().values().stream()
                .filter(record -> record.getIdempotentKey().equals(idempotentKey))
                .collect(Collectors.toList()));
    }

    @Override
    public Uni<Optional<IdempotentRecordDO>> findByKeyAndEntityType(final String idempotentKey,
                                                                                  final String entityType) {
        return Uni.createFrom().item(getRepo().values().stream()
                .filter(record -> record.getIdempotentKey().equals(idempotentKey)
                        && record.getEntityType().equals(entityType))
                .findFirst());
    }
}
