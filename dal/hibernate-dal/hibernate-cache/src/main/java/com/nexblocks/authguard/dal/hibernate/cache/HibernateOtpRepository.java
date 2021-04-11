package com.nexblocks.authguard.dal.hibernate.cache;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.cache.OtpRepository;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.OneTimePasswordDO;

public class HibernateOtpRepository extends AbstractHibernateRepository<OneTimePasswordDO>
        implements OtpRepository {

    @Inject
    public HibernateOtpRepository(final QueryExecutor queryExecutor) {
        super(OneTimePasswordDO.class, queryExecutor);
    }
}
