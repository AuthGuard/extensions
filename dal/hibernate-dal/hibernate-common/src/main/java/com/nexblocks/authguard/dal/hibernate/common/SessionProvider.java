package com.nexblocks.authguard.dal.hibernate.common;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Singleton
public class SessionProvider {
    private static SessionFactory factory = new Configuration()
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
            .addAnnotatedClass(TokenRestrictionsDO.class)
            .buildSessionFactory();

    static Session newSession() {
        return factory.openSession();
    }
}
