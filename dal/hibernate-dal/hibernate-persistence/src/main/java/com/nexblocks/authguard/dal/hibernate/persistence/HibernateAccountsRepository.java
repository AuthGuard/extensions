package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HibernateAccountsRepository extends AbstractHibernateRepository<AccountDO>
        implements AccountsRepository {
    private static final String GET_BY_ID = "accounts.getById";
    private static final String GET_BY_EXTERNAL_ID = "accounts.getByExternalId";
    private static final String GET_BY_EMAIL = "accounts.getByEmail";
    private static final String GET_BY_ROLE = "accounts.getByRole";

    private static final String ID_FIELD = "id";
    private static final String EXTERNAL_ID_FIELD = "externalId";
    private static final String EMAIL_FIELD = "email";
    private static final String ROLE_FIELD = "role";
    private static final String DOMAIN_FIELD = "domain";

    @Inject
    public HibernateAccountsRepository(final QueryExecutor queryExecutor) {
        super(AccountDO.class, queryExecutor);
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
        return super.update(entity)
                .exceptionally(e -> {
                    throw mapExceptions(e);
                });
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getById(final String id) {
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
    public CompletableFuture<List<AccountDO>> getByRole(final String role, final String domain) {
        return queryExecutor.getAList(session -> session.createNamedQuery(GET_BY_ROLE, AccountDO.class)
                .setParameter(ROLE_FIELD, role)
                .setParameter(DOMAIN_FIELD, domain));
    }

    private RuntimeException mapExceptions(final Throwable e) {
        if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException) {
            final String innerCauseMessage = Optional.of(e.getCause())
                    .map(Throwable::getCause)
                    .map(Throwable::getMessage)
                    .map(String::toLowerCase)
                    .orElse(null);

            if (innerCauseMessage != null) {
                if (innerCauseMessage.contains("email_dup")) {
                    return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_EMAILS, "Email already exists");
                } else if (innerCauseMessage.contains("backup_email_dup")) {
                    return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_EMAILS, "Backup email already exists");
                } else if (innerCauseMessage.contains("phone_number_dup")) {
                    return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_PHONE_NUMBER, "Phone number already exists");
                }
            }
        }

        return new RuntimeException(e);
    }
}
