package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.persistence.CredentialsRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Singleton
public class MockCredentialsRepository extends AbstractRepository<CredentialsDO> implements CredentialsRepository {

    private Map<String, String> identifiersToAccountId = new HashMap<>();

    @Override
    public CompletableFuture<CredentialsDO> save(final CredentialsDO credentials) {
        credentials.getIdentifiers()
                .stream()
                .filter(identifier -> {
                    if (identifiersToAccountId.containsKey(identifier.getIdentifier())) {
                        throw new IllegalArgumentException("Duplicate identifier found " + identifier.getIdentifier());
                    }

                    return true;
                })
                .forEach(identifier -> identifiersToAccountId.put(identifier.getIdentifier(), credentials.getAccountId()));

        return super.save(credentials);
    }

    @Override
    public CompletableFuture<Optional<CredentialsDO>> update(final CredentialsDO credentials) {
        credentials.getIdentifiers()
                .stream()
                .filter(identifier -> {
                    final String existing = identifiersToAccountId.get(identifier.getIdentifier());

                    if (existing != null && !existing.equals(credentials.getAccountId())) {
                        throw new IllegalArgumentException("Duplicate identifier " + identifier.getIdentifier());
                    }

                    return true;
                })
                .forEach(identifier -> identifiersToAccountId.putIfAbsent(identifier.getIdentifier(), credentials.getAccountId()));

        return super.update(credentials);
    }

    @Override
    public CompletableFuture<Optional<CredentialsDO>> findByIdentifier(final String identifier) {
        return CompletableFuture.supplyAsync(() -> getRepo()
                .values()
                .stream()
                .filter(credentials -> hasIdentifier(credentials, identifier))
                .findFirst());
    }

    private boolean hasIdentifier(final CredentialsDO credentials, final String identifier) {
        return credentials.getIdentifiers().stream()
                .map(UserIdentifierDO::getIdentifier)
                .anyMatch(identifier::equals);
    }
}
