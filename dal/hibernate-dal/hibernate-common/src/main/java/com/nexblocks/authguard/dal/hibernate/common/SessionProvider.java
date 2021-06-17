package com.nexblocks.authguard.dal.hibernate.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nexblocks.authguard.config.ConfigContext;
import com.nexblocks.authguard.dal.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

@Singleton
public class SessionProvider {
    private final SessionFactory factory;

    @Inject
    public SessionProvider(final @Named("hibernate") ConfigContext hibernateConfig) {
        this(hibernateConfig.asProperties());
    }

    public SessionProvider(final Properties hibernateProperties) {
        final Configuration configuration = entityMapping(new Configuration())
                .addProperties(hibernateProperties);

        factory = configuration.buildSessionFactory();
    }

    private Configuration entityMapping(final Configuration configuration) {
        return configuration.addAnnotatedClass(AccountDO.class)
                .addAnnotatedClass(EmailDO.class)
                .addAnnotatedClass(CredentialsDO.class)
                .addAnnotatedClass(UserIdentifierDO.class)
                .addAnnotatedClass(RoleDO.class)
                .addAnnotatedClass(PermissionDO.class)
                .addAnnotatedClass(AccountLockDO.class)
                .addAnnotatedClass(CredentialsAuditDO.class)
                .addAnnotatedClass(AppDO.class)
                .addAnnotatedClass(ApiKeyDO.class)
                .addAnnotatedClass(ExchangeAttemptDO.class)
                .addAnnotatedClass(AccountTokenDO.class)
                .addAnnotatedClass(SessionDO.class)
                .addAnnotatedClass(OneTimePasswordDO.class)
                .addAnnotatedClass(IdempotentRecordDO.class)
                .addAnnotatedClass(TokenRestrictionsDO.class);
    }

    Session newSession() {
        return factory.openSession();
    }
}
