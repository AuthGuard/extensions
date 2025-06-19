package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.AppDO;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.persistence.ApplicationsRepository;
import com.nexblocks.authguard.dal.persistence.Page;

import java.util.List;
import java.util.Optional;

import com.nexblocks.authguard.service.exceptions.ServiceNotFoundException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;
import io.smallrye.mutiny.Uni;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class MockApplicationsRepository extends AbstractRepository<AppDO> implements ApplicationsRepository {
    @Override
    public Uni<Optional<AppDO>> getByExternalId(final String externalId) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(app -> app.getExternalId() != null)
                .filter(app -> app.getExternalId().equals(externalId))
                .findFirst());
    }

    @Override
    public Uni<List<AppDO>> getAllForAccount(final long accountId, final Page<Long> page) {
        return Uni.createFrom().item(() -> getRepo().values()
                .stream()
                .filter(app -> app.getParentAccountId().equals(accountId) && app.getId() > page.getCursor())
                .limit(page.getCount())
                .collect(Collectors.toList()));
    }

    @Override
    public Uni<AppDO> addAppPermissions(final AppDO app, final List<PermissionDO> permissions) {
        return getById(app.getId())
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.ACCOUNT_DOES_NOT_EXIST, "")))
                .flatMap(retrieved -> {
                    Set<PermissionDO> combinedPermissions = Stream.concat(app.getPermissions().stream(), permissions.stream())
                            .collect(Collectors.toSet());

                    retrieved.setPermissions(combinedPermissions);

                    return super.update(retrieved).map(Optional::get);
                });
    }

    @Override
    public Uni<AppDO> removeAppPermissions(final AppDO app, final List<PermissionDO> permissions) {
        return getById(app.getId())
                .map(opt -> opt.orElseThrow(() -> new ServiceNotFoundException(ErrorCode.APP_DOES_NOT_EXIST, "")))
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
}
