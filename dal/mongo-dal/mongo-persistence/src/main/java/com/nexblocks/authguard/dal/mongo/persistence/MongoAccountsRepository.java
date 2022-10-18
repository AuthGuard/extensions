package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MongoAccountsRepository extends AbstractMongoRepository<AccountDO> implements AccountsRepository {
    private static final String COLLECTION_KEY = "accounts";

    @Inject
    public MongoAccountsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.ACCOUNTS, AccountDO.class);
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> update(AccountDO record) {
        try {
            return super.update(record);
        } catch (final MongoWriteException e) {
            throw mapWriteErrors(e);
        }
    }

    @Override
    public CompletableFuture<AccountDO> save(final AccountDO account) {
        try {
            return super.save(account);
        } catch (final MongoWriteException e) {
            throw mapWriteErrors(e);
        }
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByExternalId(final String externalId) {
        return facade.findOne(Filters.eq("externalId", externalId));
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> getByEmail(final String email, final String domain) {
        return facade.findOne(Filters.and(
                Filters.eq("email.email", email),
                Filters.eq("domain", domain)
        ));
    }

    @Override
    public CompletableFuture<List<AccountDO>> getByRole(final String role, final String domain) {
        return facade.find(Filters.and(
                Filters.in("roles", role),
                Filters.eq("domain", domain)
        ));
    }

    @Override
    public CompletableFuture<Optional<AccountDO>> findByIdentifier(final String identifier, final String domain) {
        return facade.findOne(Filters.and(
                Filters.in("identifiers.identifier", identifier),
                Filters.eq("domain", domain)
        ));
    }

    private RuntimeException mapWriteErrors(final MongoWriteException e) {
        if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
            return mapDuplicateError(e);
        }

        return e;
    }

    private RuntimeException mapDuplicateError(final MongoWriteException e) {
        if (e.getMessage().contains("email")) {
            return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_EMAILS, "Email already exists");
        }

        if (e.getMessage().contains("phone")) {
            return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_PHONE_NUMBER, "Phone number already exists");
        }

        if (e.getMessage().contains("backupEmail")) {
            return new ServiceConflictException(ErrorCode.ACCOUNT_DUPLICATE_EMAILS, "Backup email already exists");
        }

        if (e.getMessage().contains("identifier")) {
            return new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Identifier already exists");
        }

        return e;
    }
}
