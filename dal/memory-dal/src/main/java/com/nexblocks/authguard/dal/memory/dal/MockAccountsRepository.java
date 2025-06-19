package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AccountDO;
import com.nexblocks.authguard.dal.model.PasswordDO;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.persistence.AccountsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.ServiceNotFoundException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;
import io.smallrye.mutiny.Uni;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class MockAccountsRepository extends AbstractRepository<AccountDO> implements AccountsRepository {
    private Map<String, Long> identifiersToAccountId = new HashMap<>();

    @Override
    public Uni<AccountDO> save(final AccountDO account) {
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
    public Uni<Optional<AccountDO>> update(final AccountDO account) {
        account.getIdentifiers()
                .stream()
                .filter(identifier -> {
                    final Long existing = identifiersToAccountId.get(identifier.getIdentifier());

                    if (existing != null && !existing.equals(account.getId())) {
                        throw new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Duplicate identifier found " + identifier.getIdentifier());
                    }

                    return true;
                })
                .forEach(identifier -> identifiersToAccountId.putIfAbsent(identifier.getIdentifier(), account.getId()));

        return super.update(account);
    }

    @Override
    public Uni<Optional<AccountDO>> getByExternalId(final String externalId) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(account -> account.getExternalId() != null)
                .filter(account -> account.getExternalId().equals(externalId))
                .findFirst());
    }

    @Override
    public Uni<Optional<AccountDO>> getByEmail(final String email, final String domain) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(account -> account.getEmail() != null)
                .filter(account -> account.getEmail().getEmail().equals(email))
                .findFirst());
    }

    @Override
    public Uni<List<AccountDO>> getByRole(final String role, final String domain) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(account -> account.getRoles().contains(role))
                .collect(Collectors.toList()));
    }

    @Override
    public Uni<Optional<AccountDO>> findByIdentifier(final String identifier, final String domain) {
        return Uni.createFrom().item(() -> getRepo()
                .values()
                .stream()
                .filter(credentials -> hasIdentifier(credentials, identifier, domain))
                .findFirst());
    }

    @Override
    public Uni<AccountDO> addAccountPermissions(final AccountDO account, final List<PermissionDO> permissions) {
        return getById(account.getId())
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "")))
                .flatMap(retrieved -> {
                    Set<PermissionDO> combinedPermissions = Stream.concat(account.getPermissions().stream(), permissions.stream())
                            .collect(Collectors.toSet());

                    retrieved.setPermissions(combinedPermissions);

                    return super.update(retrieved).map(Optional::get);
                });
    }

    @Override
    public Uni<AccountDO> removeAccountPermissions(final AccountDO account, final List<PermissionDO> permissions) {
        return getById(account.getId())
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "")))
                .flatMap(retrieved -> {
                    Set<String> permissionsFullNames = permissions.stream()
                            .map(permission -> permission.getPermissionGroup() + ":" + permission.getName())
                            .collect(Collectors.toSet());

                    Set<PermissionDO> filteredPermissions = retrieved.getPermissions().stream()
                            .filter(permission -> !permissionsFullNames.contains(permission.getPermissionGroup() + ":" + permission.getName()))
                            .collect(Collectors.toSet());

                    retrieved.setPermissions(filteredPermissions);

                    return super.update(retrieved).map(Optional::get);
                });
    }

    @Override
    public Uni<AccountDO> addUserIdentifier(final AccountDO account, final UserIdentifierDO identifier) {
        return getById(account.getId())
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "")))
                .flatMap(retrieved -> {
                    retrieved.getIdentifiers().add(identifier);

                    return super.update(retrieved).map(Optional::get);
                });
    }

    @Override
    public Uni<AccountDO> removeUserIdentifier(final AccountDO account, final UserIdentifierDO identifier) {
        return getById(account.getId())
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "")))
                .flatMap(retrieved -> {
                    retrieved.getIdentifiers()
                            .removeIf(stored -> stored.getIdentifier().equals(identifier.getIdentifier()));

                    return super.update(retrieved).map(Optional::get);
                });
    }

    @Override
    public Uni<AccountDO> replaceIdentifierInPlace(final AccountDO account, final String oldIdentifier,
                                                   final UserIdentifierDO newIdentifier) {
        return getById(account.getId())
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "")))
                .flatMap(retrieved -> {
                    retrieved.getIdentifiers()
                            .removeIf(stored -> stored.getIdentifier().equals(oldIdentifier));

                    retrieved.getIdentifiers().add(newIdentifier);

                    return super.update(retrieved).map(Optional::get);
                });
    }

    @Override
    public Uni<AccountDO> updateUserPassword(final AccountDO account, final PasswordDO hashedPassword) {
        return getById(account.getId())
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "")))
                .flatMap(retrieved -> {
                    retrieved.setHashedPassword(hashedPassword);

                    return super.update(retrieved).map(Optional::get);
                });
    }

    private boolean hasIdentifier(final AccountDO credentials, final String identifier, final String domain) {
        return credentials.getIdentifiers().stream()
                .filter(userIdentifier -> userIdentifier.getDomain().equals(domain))
                .map(UserIdentifierDO::getIdentifier)
                .anyMatch(identifier::equals);
    }
}
