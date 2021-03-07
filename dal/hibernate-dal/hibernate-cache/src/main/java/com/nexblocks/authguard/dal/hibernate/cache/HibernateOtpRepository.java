package com.nexblocks.authguard.dal.hibernate.cache;

import com.nexblocks.authguard.dal.cache.OtpRepository;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.model.OneTimePasswordDO;

public class HibernateOtpRepository extends AbstractHibernateRepository<OneTimePasswordDO>
        implements OtpRepository {

    public HibernateOtpRepository() {
        super(OneTimePasswordDO.class);
    }
}
