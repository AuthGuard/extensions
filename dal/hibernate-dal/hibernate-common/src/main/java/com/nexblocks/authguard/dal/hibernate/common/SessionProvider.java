package com.nexblocks.authguard.dal.hibernate.common;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Singleton
public class SessionProvider {
    private static final Configuration factoryConfiguration = new Configuration()
            .addAnnotatedClass(AccountDO.class)
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

    private static SessionFactory factory;

    public static void overrideProperty(final String name, final String value) {
        if (factory != null) {
            throw new IllegalStateException("Properties cannot be overridden after the session factory was built");
        }

        factoryConfiguration.setProperty(name, value);
    }

    static Session newSession() {
        if (factory == null) {
            factory = factoryConfiguration.buildSessionFactory();
        }

        return factory.openSession();
    }
}
