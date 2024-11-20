package com.nexblocks.authguard.dal.memory.dal;

import com.nexblocks.authguard.dal.cache.SessionsRepository;
import com.nexblocks.authguard.dal.model.SessionDO;

import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class MockSessionsRepository extends AbstractRepository<SessionDO>
        implements SessionsRepository {
    @Override
    public CompletableFuture<Optional<SessionDO>> getByToken(final String token) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(session -> session.getSessionToken().equals(token))
                .findFirst());
    }

    @Override
    public CompletableFuture<Optional<SessionDO>> deleteByToken(final String sessionToken) {
        return getByToken(sessionToken)
                .thenApply(opt -> {
                    opt.ifPresent(session -> getRepo().remove(session.getId()));
                    return opt;
                });
    }

    @Override
    public CompletableFuture<List<SessionDO>> findByAccountId(final long accountId, final String domain) {
        return CompletableFuture.supplyAsync(() -> getRepo().values()
                .stream()
                .filter(session -> Objects.equals(accountId, session.getAccountId())
                        && Objects.equals(domain, session.getDomain()))
                .collect(Collectors.toList()));
    }
}
