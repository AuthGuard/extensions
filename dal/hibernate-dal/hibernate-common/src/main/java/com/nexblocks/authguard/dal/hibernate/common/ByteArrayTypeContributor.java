package com.nexblocks.authguard.dal.hibernate.common;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayJavaType;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;
import org.hibernate.type.internal.BasicTypeImpl;


public class ByteArrayTypeContributor implements TypeContributor {
    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        BasicType<?> byteArrayType = new BasicTypeImpl<>(
                new PrimitiveByteArrayJavaType(),
                new VarbinaryJdbcType()
        );

        typeContributions.contributeType(byteArrayType);
    }
}
