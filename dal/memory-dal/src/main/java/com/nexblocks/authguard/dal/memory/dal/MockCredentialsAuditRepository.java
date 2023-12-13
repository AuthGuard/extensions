package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.CredentialsAuditDO;
import com.nexblocks.authguard.dal.persistence.CredentialsAuditRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockCredentialsAuditRepository extends AbstractRepository<CredentialsAuditDO>
        implements CredentialsAuditRepository {

    @Override
    public CompletableFuture<List<CredentialsAuditDO>> findByCredentialsId(final long credentialsId) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(record -> record.getCredentialsId() == credentialsId)
                .collect(Collectors.toList()));
    }
}
