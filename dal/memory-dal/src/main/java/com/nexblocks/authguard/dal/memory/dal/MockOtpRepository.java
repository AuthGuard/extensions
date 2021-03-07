package com.nexblocks.authguard.dal.memory.dal;

import com.google.inject.Singleton;
import com.nexblocks.authguard.dal.cache.OtpRepository;
import com.nexblocks.authguard.dal.model.OneTimePasswordDO;

@Singleton
public class MockOtpRepository extends AbstractRepository<OneTimePasswordDO> implements OtpRepository {
}
