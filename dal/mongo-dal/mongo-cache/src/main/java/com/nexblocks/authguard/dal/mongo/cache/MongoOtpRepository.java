package com.nexblocks.authguard.dal.mongo.cache;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.cache.OtpRepository;
import com.nexblocks.authguard.dal.model.OneTimePasswordDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;

public class MongoOtpRepository extends AbstractMongoRepository<OneTimePasswordDO> implements OtpRepository {
    private static final String COLLECTION_KEY = "otp";

    @Inject
    public MongoOtpRepository(final MongoClientWrapper clientWrapper) {
        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.OTP, OneTimePasswordDO.class);
    }
}
