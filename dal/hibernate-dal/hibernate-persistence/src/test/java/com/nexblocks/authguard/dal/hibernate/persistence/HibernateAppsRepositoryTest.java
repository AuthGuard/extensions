package com.nexblocks.authguard.dal.hibernate.persistence;

import com.nexblocks.authguard.dal.hibernate.common.ReactiveQueryExecutor;
import com.nexblocks.authguard.dal.hibernate.common.SessionProvider;
import com.nexblocks.authguard.dal.model.AppDO;
import com.nexblocks.authguard.dal.model.PermissionDO;
import com.nexblocks.authguard.dal.persistence.LongPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateAppsRepositoryTest {
    private HibernateAppsRepository repository;
    private HibernatePermissionsRepository permissionsRepository;

    @BeforeAll
    public void setup() {
        final SessionProvider sessionProvider = TestSessionProvider.create();
        initialize(sessionProvider);
    }

    protected void initialize(final SessionProvider sessionProvider) {
        ReactiveQueryExecutor queryExecutor = new ReactiveQueryExecutor(sessionProvider);

        repository = new HibernateAppsRepository(queryExecutor);
        permissionsRepository = new HibernatePermissionsRepository(queryExecutor);
    }

    @Test
    public void saveAndGetById() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        final AppDO app = AppDO.builder()
                .id(id)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AppDO persisted = repository.save(app).subscribeAsCompletionStage().join();
        final Optional<AppDO> retrieved = repository.getById(id).subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void getByExternalId() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        final String externalId = "getByExternalId";

        final AppDO app = AppDO.builder()
                .id(id)
                .externalId(externalId)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AppDO persisted = repository.save(app).subscribeAsCompletionStage().join();
        final Optional<AppDO> retrieved = repository.getByExternalId(externalId).subscribeAsCompletionStage().join();

        assertThat(retrieved).contains(persisted);
    }

    @Test
    public void getAllForAccount() {
        final long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        final AppDO app = AppDO.builder()
                .id(id)
                .parentAccountId(101L)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .build();

        final AppDO persisted = repository.save(app).subscribeAsCompletionStage().join();
        final List<AppDO> retrieved = repository.getAllForAccount(101, LongPage.of(null, 20)).subscribeAsCompletionStage().join();

        assertThat(retrieved).containsOnly(persisted);
    }

    @Test
    public void addAndRemoveAppPermission() {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        AppDO app = AppDO.builder()
                .id(id)
                .roles(Collections.emptySet())
                .permissions(Collections.emptySet())
                .domain("main")
                .build();

        AppDO persisted = repository.save(app).subscribeAsCompletionStage().join();

        PermissionDO firstPermission = permissionsRepository.save(
                        PermissionDO.builder()
                                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                                .domain("main")
                                .forApplications(true)
                                .permissionGroup("app_tests")
                                .name("read")
                                .build()
                )
                .subscribeAsCompletionStage()
                .join();

        PermissionDO secondPermission = permissionsRepository.save(
                        PermissionDO.builder()
                                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                                .domain("main")
                                .forAccounts(true)
                                .permissionGroup("app_tests")
                                .name("write")
                                .build()
                )
                .subscribeAsCompletionStage()
                .join();

        // add
        repository.addAppPermissions(persisted, Arrays.asList(firstPermission, secondPermission))
                .subscribeAsCompletionStage()
                .join();

        Optional<AppDO> afterAdd = repository.getById(persisted.getId()).subscribeAsCompletionStage().join();

        assertThat(afterAdd).isNotEmpty();
        assertThat(afterAdd.get().getPermissions())
                .containsExactlyInAnyOrder(firstPermission, secondPermission);

        // remove one
        repository.removeAppPermissions(persisted, Collections.singletonList(firstPermission))
                .subscribeAsCompletionStage()
                .join();

        Optional<AppDO> afterRemove = repository.getById(persisted.getId()).subscribeAsCompletionStage().join();

        assertThat(afterRemove).isNotEmpty();
        assertThat(afterRemove.get().getPermissions())
                .containsExactlyInAnyOrder(secondPermission);
    }
}