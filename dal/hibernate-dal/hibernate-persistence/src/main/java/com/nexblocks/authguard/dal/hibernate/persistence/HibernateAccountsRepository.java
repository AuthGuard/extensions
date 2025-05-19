package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class HibernateAccountsRepository extends AbstractHibernateRepository<AccountDO>
        implements AccountsRepository {
    private static final String GET_BY_ID = "accounts.getById";
    private static final String GET_BY_EXTERNAL_ID = "accounts.getByExternalId";
    private static final String GET_BY_EMAIL = "accounts.getByEmail";
    private static final String GET_BY_IDENTIFIER = "accounts.getByIdentifier";
    private static final String GET_BY_ROLE = "accounts.getByRole";

    private static final String ID_FIELD = "id";
    private static final String EXTERNAL_ID_FIELD = "externalId";
    private static final String EMAIL_FIELD = "email";
    private static final String IDENTIFIER_FIELD = "identifier";
    private static final String ROLE_FIELD = "role";
    private static final String DOMAIN_FIELD = "domain";

    private final HibernateUserIdentifiersRepository userIdentifiersRepository;

    @Inject
    public HibernateAccountsRepository(final ReactiveQueryExecutor queryExecutor) {
        super(AccountDO.class, queryExecutor);

        this.userIdentifiersRepository = new HibernateUserIdentifiersRepository(queryExecutor);
    }

    @Override
    public Uni<AccountDO> save(final AccountDO entity) {
        return super.save(entity);
    }

    @Override
    public Uni<Optional<AccountDO>> update(final AccountDO entity) {
        return super.getById(entity.getId())
                .map(Optional::get)
                .map(existing -> {
                    final Set<String> newIdentifiers = entity.getIdentifiers()
                            .stream()
                            .map(UserIdentifierDO::getIdentifier)
                            .collect(Collectors.toSet());

                    // get the difference
                    return existing.getIdentifiers()
                            .stream()
                            .filter(e -> !newIdentifiers.contains(e.getIdentifier()))
                            .collect(Collectors.toSet());
                })
                .flatMap(difference -> super.update(entity)
                        .map(ignored -> difference))
                .flatMap(identifiers -> Uni.createFrom().completionStage(userIdentifiersRepository.deleteAll(identifiers)))
                .map(ignored -> Optional.of(entity));
    }

    @Override
    public Uni<Optional<AccountDO>> getById(final long id) {
        return queryExecutor.getSingleResult(GET_BY_ID, AccountDO.class, ID_FIELD, id);
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByExternalId(final String externalId) {
        return queryExecutor.getSingleResult(GET_BY_EXTERNAL_ID, AccountDO.class, EXTERNAL_ID_FIELD, externalId)
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByEmail(final String email, final String domain) {
        return queryExecutor.getSingleResult(GET_BY_EMAIL, AccountDO.class,
                        EMAIL_FIELD, email,
                        DOMAIN_FIELD, domain)
                .subscribeAsCompletionStage();
    }

    @Override
    public Uni<Optional<AccountDO>> findByIdentifier(final String identifier, final String domain) {
        return queryExecutor.getSingleResult(GET_BY_IDENTIFIER, AccountDO.class,
                        IDENTIFIER_FIELD, identifier,
                        DOMAIN_FIELD, domain);
    }

    @Override
    public Uni<List<AccountDO>> getByRole(final String role, final String domain) {
        return queryExecutor.getAList(GET_BY_ROLE, AccountDO.class,
                ROLE_FIELD, role,
                DOMAIN_FIELD, domain);
    }
}
