package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
    public HibernateAccountsRepository(final QueryExecutor queryExecutor) {
        super(AccountDO.class, queryExecutor);
        this.userIdentifiersRepository = new HibernateUserIdentifiersRepository(queryExecutor);
    }

    @Override
    public CompletableFuture<AccountDO> save(final AccountDO entity) {
        return super.save(entity)
                .exceptionally(e -> {
                    throw mapExceptions(e);
                });
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> update(final AccountDO entity) {
        return super.getById(entity.getId())
                .thenApply(Optional::get)
                .thenApply(existing -> {
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
                .thenCompose(difference -> super.update(entity)
                        .thenApply(ignored -> difference))
                .thenCompose(userIdentifiersRepository::deleteAll)
                .thenApply(ignored -> Optional.of(entity))
                .exceptionally(e -> {
                    throw mapExceptions(e);
                });
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getById(final long id) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_ID, AccountDO.class)
                .setParameter(ID_FIELD, id));
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByExternalId(final String externalId) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_EXTERNAL_ID, AccountDO.class)
                .setParameter(EXTERNAL_ID_FIELD, externalId));
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByEmail(final String email, final String domain) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_EMAIL, AccountDO.class)
                .setParameter(EMAIL_FIELD, email)
                .setParameter(DOMAIN_FIELD, domain));
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> findByIdentifier(final String identifier, final String domain) {
        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_IDENTIFIER, AccountDO.class)
                .setParameter(IDENTIFIER_FIELD, identifier)
                .setParameter(DOMAIN_FIELD, domain));
    }

    @Override
    public CompletableFuture<List<AccountDO>> getByRole(final String role, final String domain) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_ROLE, AccountDO.class)
                .setParameter(ROLE_FIELD, role)
                .setParameter(DOMAIN_FIELD, domain));
    }

    private RuntimeException mapExceptions(final Throwable e) {
        final Throwable effectiveException = e instanceof CompletionException
                ? e.getCause()
                : e;

        if (effectiveException.getCause() != null && effectiveException.getCause() instanceof ConstraintViolationException) {
            return mapException((ConstraintViolationException) effectiveException.getCause());
        }

        return new RuntimeException(e);
    }

    private RuntimeException mapException(final ConstraintViolationException e) {
        // check the constraint name first
        final String constraintName = e.getConstraintName();
        RuntimeException mappedException;

        if (constraintName != null) {
            mappedException = mapConstraintName(constraintName.toLowerCase());

            if (mappedException != null) {
                return mappedException;
            }
        }

        // then the first cause message
        final String outerCauseMessage = Optional.of(e.getCause())
                .map(Throwable::getMessage)
                .map(String::toLowerCase)
                .orElse(null);

        if (outerCauseMessage != null) {
            mappedException = mapConstraintName(outerCauseMessage);

            if (mappedException != null) {
                return mappedException;
            }
        }

        // then the inner cause message
        final String innerCauseMessage = Optional.of(e.getCause())
                .map(Throwable::getCause)
                .map(Throwable::getMessage)
                .map(String::toLowerCase)
                .orElse(null);

        if (innerCauseMessage != null) {
            mappedException = mapConstraintName(innerCauseMessage);

            if (mappedException != null) {
                return mappedException;
            }
        }

        return new RuntimeException(e);
    }

    private RuntimeException mapConstraintName(final String constraintName) {
        if (constraintName.contains("email_dup")) {
            return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_EMAILS, "Email already exists");
        } else if (constraintName.contains("backup_email_dup")) {
            return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_EMAILS, "Backup email already exists");
        } else if (constraintName.contains("phone_number_dup")) {
            return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_PHONE_NUMBER, "Phone number already exists");
        } else if (constraintName.contains("identifier_dup")) {
            return new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Identifier already exists");
        }

        return null;
    }
}
