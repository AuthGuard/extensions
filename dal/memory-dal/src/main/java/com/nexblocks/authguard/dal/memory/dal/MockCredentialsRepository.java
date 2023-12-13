package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.persistence.CredentialsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Singleton
public class MockCredentialsRepository extends AbstractRepository<CredentialsDO> implements CredentialsRepository {

    private Map<String, Long> identifiersToAccountId = new HashMap<>();

    @Override
    public CompletableFuture<CredentialsDO> save(final CredentialsDO credentials) {
        credentials.getIdentifiers()
                .stream()
                .filter(identifier -> {
                    if (identifiersToAccountId.containsKey(identifier.getIdentifier())) {
                        throw new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Duplicate identifier found " + identifier.getIdentifier());
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
                    final Long existing = identifiersToAccountId.get(identifier.getIdentifier());

                    if (existing != null && !existing.equals(credentials.getAccountId())) {
                        throw new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Duplicate identifier found " + identifier.getIdentifier());
                    }

                    return true;
                })
                .forEach(identifier -> identifiersToAccountId.putIfAbsent(identifier.getIdentifier(), credentials.getAccountId()));

        return super.update(credentials);
    }

    @Override
    public CompletableFuture<Optional<CredentialsDO>> findByIdentifier(final String identifier, final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo()
                .values()
                .stream()
                .filter(credentials -> hasIdentifier(credentials, identifier, domain))
                .findFirst());
    }

    private boolean hasIdentifier(final CredentialsDO credentials, final String identifier, final String domain) {
        return credentials.getIdentifiers().stream()
                .filter(userIdentifier -> userIdentifier.getDomain().equals(domain))
                .map(UserIdentifierDO::getIdentifier)
                .anyMatch(identifier::equals);
    }
}
