package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockAccountsRepository extends AbstractRepository<AccountDO> implements AccountsRepository {
    @Override
    public CompletableFuture<Optional<AccountDO>> getByExternalId(final String externalId) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(account -> account.getExternalId() != null)
                .filter(account -> account.getExternalId().equals(externalId))
                .findFirst());
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByEmail(final String email) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(account -> account.getEmail() != null)
                .filter(account -> account.getEmail().getEmail().equals(email))
                .findFirst());
    }

    @Override
    public CompletableFuture<List<AccountDO>> getByRole(final String role) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(account -> account.getRoles().contains(role))
                .collect(Collectors.toList()));
    }
}
