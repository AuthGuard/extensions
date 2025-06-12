package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.cache.AccountTokensRepository;
import com.nexblocks.authguard.dal.model.AccountTokenDO;

import java.util.Optional;
import io.smallrye.mutiny.Uni;

@Singleton
public class MockAccountsTokensRepository extends AbstractRepository<AccountTokenDO> implements AccountTokensRepository {

    @Override
    public Uni<Optional<AccountTokenDO>> getByToken(final String token) {
        return Uni.createFrom().item(() -> getRepo().values().stream()
                .filter(accountToken -> accountToken.getToken().equals(token))
                .findFirst());
    }

    @Override
    public Uni<Optional<AccountTokenDO>> deleteToken(final String token) {
        return getByToken(token)
                .map(opt -> {
                    opt.ifPresent(accountToken -> getRepo().remove(accountToken.getId()));
                    return opt;
                });
    }
}
