package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.cache.AccountTokensRepository;
import com.nexblocks.authguard.dal.model.AccountTokenDO;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Singleton
public class MockAccountsTokensRepository extends AbstractRepository<AccountTokenDO> implements AccountTokensRepository {

    @Override
    public CompletableFuture<Optional<AccountTokenDO>> getByToken(final String token) {
        return CompletableFuture.supplyAsync(() -> getRepo().values().stream()
                .filter(accountToken -> accountToken.getToken().equals(token))
                .findFirst());
    }

    @Override
    public CompletableFuture<Optional<AccountTokenDO>> deleteToken(final String token) {
        return getByToken(token)
                .thenApply(opt -> {
                    opt.ifPresent(accountToken -> getRepo().remove(accountToken.getId()));
                    return opt;
                });
    }
}
