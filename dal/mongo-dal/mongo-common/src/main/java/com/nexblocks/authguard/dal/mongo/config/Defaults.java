package com.nexblocks.authguard.dal.mongo.config;

public class Defaults {
    public static final String DATABASE = "authguard";
    public static final Long TIMEOUT = 2000L;

    public static class Collections {
        public static final String ACCOUNTS = "accounts";
        public static final String API_KEYS = "api_keys";
        public static final String CREDENTIALS = "credentials";
        public static final String CREDENTIALS_AUDIT = "credentials_audit";
        public static final String APPLICATIONS = "apps";
        public static final String ACCOUNT_TOKENS = "tokens";
        public static final String OTP = "otp";
        public static final String PERMISSIONS = "permissions";
        public static final String ROLES = "roles";
        public static final String SESSIONS = "sessions";
    }
}
