package com.nexblocks.authguard.dal.mongo.persistence;

import com.google.inject.Inject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.mongo.common.AbstractMongoRepository;
import com.nexblocks.authguard.dal.mongo.common.setup.MongoClientWrapper;
import com.nexblocks.authguard.dal.mongo.config.Defaults;
import com.nexblocks.authguard.dal.persistence.CredentialsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

//public class MongoCredentialsRepostiory extends AbstractMongoRepository<CredentialsDO> implements CredentialsRepository {
//    private static final String COLLECTION_KEY = "credentials";
//
//    @Inject
//    public MongoCredentialsRepostiory(final MongoClientWrapper clientWrapper) {
//        super(clientWrapper, COLLECTION_KEY, Defaults.Collections.CREDENTIALS, CredentialsDO.class);
//    }
//
//    @Override
//    public CompletableFuture<CredentialsDO> save(final CredentialsDO credentials) {
//        try {
//            return super.save(credentials);
//        } catch (final MongoWriteException e) {
//            if (e.getMessage().contains("identifier")) {
//                throw new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Duplicate identifiers");
//            }
//
//            throw e;
//        }
//    }
//
//    @Override
//    public CompletableFuture<Optional<CredentialsDO>> findByIdentifier(final String identifier, final String domain) {
//        return facade.findOne(Filters.and(
//                Filters.in("identifiers.identifier", identifier),
//                Filters.eq("identifiers.domain", domain)
//        ));
//    }
//}
