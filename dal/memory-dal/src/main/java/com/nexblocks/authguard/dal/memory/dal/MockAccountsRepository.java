package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockAccountsRepository extends AbstractRepository<AccountDO> implements AccountsRepository {
    private Map<String, String> identifiersToAccountId = new HashMap<>();

    @Override
    public CompletableFuture<AccountDO> save(final AccountDO account) {
        account.getIdentifiers()
                .stream()
                .peek(identifier -> {
                    if (identifiersToAccountId.containsKey(identifier.getIdentifier())) {
                        throw new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Duplicate identifier found " + identifier.getIdentifier());
                    }
                })
                .forEach(identifier -> identifiersToAccountId.put(identifier.getIdentifier(), account.getId()));

        return super.save(account);
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> update(final AccountDO account) {
        account.getIdentifiers()
                .stream()
                .filter(identifier -> {
                    final String existing = identifiersToAccountId.get(identifier.getIdentifier());

                    if (existing != null && !existing.equals(account.getId())) {
                        throw new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Duplicate identifier found " + identifier.getIdentifier());
                    }

                    return true;
                })
                .forEach(identifier -> identifiersToAccountId.putIfAbsent(identifier.getIdentifier(), account.getId()));

        return super.update(account);
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByExternalId(final String externalId) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(account -> account.getExternalId() != null)
                .filter(account -> account.getExternalId().equals(externalId))
                .findFirst());
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByEmail(final String email, final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(account -> account.getEmail() != null)
                .filter(account -> account.getEmail().getEmail().equals(email))
                .findFirst());
    }

    @Override
    public CompletableFuture<List<AccountDO>> getByRole(final String role, final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(account -> account.getRoles().contains(role))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> findByIdentifier(final String identifier, final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo()
                .values()
                .stream()
                .filter(credentials -> hasIdentifier(credentials, identifier, domain))
                .findFirst());
    }

    private boolean hasIdentifier(final AccountDO credentials, final String identifier, final String domain) {
        return credentials.getIdentifiers().stream()
                .filter(userIdentifier -> userIdentifier.getDomain().equals(domain))
                .map(UserIdentifierDO::getIdentifier)
                .anyMatch(identifier::equals);
    }
}
