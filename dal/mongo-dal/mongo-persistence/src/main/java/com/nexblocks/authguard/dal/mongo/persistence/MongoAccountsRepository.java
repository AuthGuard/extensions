package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.PasswordDO;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.ServiceNotFoundException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MongoAccountsRepository extends AbstractMongoRepository<AccountDO> implements AccountsRepository {
    private static final String COLLECTION_KEY = "accounts";

    @Inject
    public MongoAccountsRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.ACCOUNTS, AccountDO.class);
    }

    @Override
    public Uni<Optional<AccountDO>> update(AccountDO record) {
        return super.update(record)
                .onFailure()
                .transform(throwable -> {
                    if (throwable instanceof MongoWriteException) {
                        return mapWriteErrors((MongoWriteException) throwable);
                    }

                    return throwable;
                });
    }

    @Override
    public Uni<AccountDO> save(final AccountDO account) {
        return super.save(account)
                .onFailure()
                .transform(throwable -> {
                    if (throwable instanceof MongoWriteException) {
                        return mapWriteErrors((MongoWriteException) throwable);
                    }

                    return throwable;
                });
    }

    @Override
    public Uni<Optional<AccountDO>> getByExternalId(final String externalId) {
        return facade.findOne(Filters.eq("externalId", externalId));
    }

    @Override
    public Uni<Optional<AccountDO>> getByEmail(final String email, final String domain) {
        return facade.findOne(Filters.and(
                Filters.eq("email.email", email),
                Filters.eq("domain", domain)
        ));
    }

    @Override
    public Uni<List<AccountDO>> getByRole(final String role, final String domain) {
        return facade.find(Filters.and(
                Filters.in("roles", role),
                Filters.eq("domain", domain)
        ));
    }

    @Override
    public Uni<Optional<AccountDO>> findByIdentifier(final String identifier, final String domain) {
        return facade.findOne(Filters.and(
                Filters.in("identifiers.identifier", identifier),
                Filters.eq("domain", domain)
        ));
    }

    @Override
    public Uni<AccountDO> addAccountPermissions(final AccountDO account, final List<PermissionDO> permissions) {
        return getById(account.getId())
                .map(opt -> opt
                        .map(retrieved -> {
                            Set<PermissionDO> combinedPermissions = Stream.concat(account.getPermissions().stream(), permissions.stream())
                                    .collect(Collectors.toSet());

                            retrieved.setPermissions(combinedPermissions);

                            return retrieved;
                        })
                        .orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "Account does not exist")))
                .flatMap(updated -> facade.replaceById(account.getId(), updated))
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "Account does not exist")));
    }

    @Override
    public Uni<AccountDO> removeAccountPermissions(final AccountDO account, final List<PermissionDO> permissions) {
        return getById(account.getId())
                .map(opt -> opt
                        .map(retrieved -> {
                            Set<String> permissionsFullNames = permissions.stream()
                                    .map(permission -> permission.getPermissionGroup() + ":" + permission.getName())
                                    .collect(Collectors.toSet());

                            Set<PermissionDO> filteredPermissions = retrieved.getPermissions().stream()
                                    .filter(permission -> !permissionsFullNames.contains(permission.getPermissionGroup() + ":" + permission.getName()))
                                    .collect(Collectors.toSet());

                            retrieved.setPermissions(filteredPermissions);

                            return retrieved;
                        })
                        .orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "Account does not exist")))
                .flatMap(updated -> facade.replaceById(account.getId(), updated))
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "Account does not exist")));
    }

    @Override
    public Uni<AccountDO> addUserIdentifier(final AccountDO account, final UserIdentifierDO identifier) {
        return facade.findByIdUnchecked(account.getId())
                .flatMap(retrieved -> {
                    retrieved.getIdentifiers().add(identifier);

                    return facade.replaceById(account.getId(), retrieved)
                            .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "Account does not exist")));
                });
    }

    @Override
    public Uni<AccountDO> removeUserIdentifier(final AccountDO account, final UserIdentifierDO identifier) {
        return facade.findByIdUnchecked(account.getId())
                .flatMap(retrieved -> {
                    retrieved.getIdentifiers()
                            .removeIf(stored -> stored.getIdentifier().equals(identifier.getIdentifier()));

                    return facade.replaceById(account.getId(), retrieved)
                            .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "Account does not exist")));
                });
    }

    @Override
    public Uni<AccountDO> replaceIdentifierInPlace(final AccountDO account, final String oldIdentifier,
                                                   final UserIdentifierDO newIdentifier) {
        return facade.findByIdUnchecked(account.getId())
                .flatMap(retrieved -> {
                    retrieved.getIdentifiers()
                            .removeIf(stored -> stored.getIdentifier().equals(oldIdentifier));

                    retrieved.getIdentifiers().add(newIdentifier);

                    return facade.replaceById(account.getId(), retrieved)
                            .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "Account does not exist")));
                });
    }

    @Override
    public Uni<AccountDO> updateUserPassword(final AccountDO account, final PasswordDO hashedPassword) {
        return facade.findByIdUnchecked(account.getId())
                .flatMap(retrieved -> {
                    retrieved.setHashedPassword(hashedPassword);

                    return facade.replaceById(account.getId(), retrieved)
                            .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "Account does not exist")));
                });
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
