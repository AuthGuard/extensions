package com.nexblocks.authguard.dal.hibernate.persistence;

import com.google.inject.Inject;
import com.nexblocks.authguard.dal.hibernate.common.AbstractHibernateRepository;
import com.nexblocks.authguard.dal.hibernate.common.CommonFields;
import com.nexblocks.authguard.dal.hibernate.common.QueryExecutor;
import com.nexblocks.authguard.dal.model.CredentialsDO;
import com.nexblocks.authguard.dal.model.UserIdentifierDO;
import com.nexblocks.authguard.dal.persistence.CredentialsRepository;
import com.nexblocks.authguard.service.exceptions.ServiceConflictException;
import com.nexblocks.authguard.service.exceptions.codes.ErrorCode;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Optional;
import java.util.Set;
import io.smallrye.mutiny.Uni;
import java.util.function.Function;
import java.util.stream.Collectors;

//public class HibernateCredentialsRepository extends AbstractHibernateRepository<CredentialsDO>
//        implements CredentialsRepository {
//    private static final String GET_BY_ID = "credentials.getById";
//    private static final String GET_BY_IDENTIFIER = "credentials.getByIdentifier";
//    private static final String IDENTIFIER_FIELD = "identifier";
//    private static final String DOMAIN_FIELD = "domain";
//
//    private final HibernateUserIdentifiersRepository userIdentifiersRepository;
//
//    @Inject
//    public HibernateCredentialsRepository(final QueryExecutor queryExecutor) {
//        super(CredentialsDO.class, queryExecutor);
//        this.userIdentifiersRepository = new HibernateUserIdentifiersRepository(queryExecutor);
//    }
//
//    @Override
//    public Uni<CredentialsDO> save(final CredentialsDO entity) {
//        return super.save(entity)
//                .exceptionally(e -> {
//                    throw mapExceptions(e);
//                });
//    }
//
//    @Override
//    public Uni<Optional<CredentialsDO>> update(final CredentialsDO entity) {
//        return super.getById(entity.getId())
//                .map(Optional::get)
//                .map(existing -> {
//                    final Set<String> newIdentifiers = entity.getIdentifiers()
//                            .stream()
//                            .map(UserIdentifierDO::getIdentifier)
//                            .collect(Collectors.toSet());
//
//                    // get the difference
//                    return existing.getIdentifiers()
//                            .stream()
//                            .filter(e -> !newIdentifiers.contains(e.getIdentifier()))
//                            .collect(Collectors.toSet());
//                })
//                .flatMap(difference -> super.update(entity)
//                        .map(ignored -> difference))
//                .flatMap(userIdentifiersRepository::deleteAll)
//                .map(ignored -> Optional.of(entity))
//                .exceptionally(e -> {
//                    throw mapExceptions(e);
//                });
//    }
//
//    @Override
//    public Uni<Optional<CredentialsDO>> getById(final long id) {
//        return queryExecutor
//                .getSingleResult(session -> session.createNamedQuery(GET_BY_ID, CredentialsDO.class)
//                        .setParameter(CommonFields.ID, id))
//                .map(Function.identity());
//    }
//
//    @Override
//    public Uni<Optional<CredentialsDO>> findByIdentifier(final String identifier, final String domain) {
//        return queryExecutor.getSingleResult(session -> session.createNamedQuery(GET_BY_IDENTIFIER, CredentialsDO.class)
//                .setParameter(IDENTIFIER_FIELD, identifier)
//                .setParameter(DOMAIN_FIELD, domain));
//    }
//
//    private RuntimeException mapExceptions(final Throwable e) {
//        if (e instanceof ConstraintViolationException) {
//            final String causeMessage = Optional.ofNullable(e.getCause())
//                    .map(Throwable::getMessage)
//                    .map(String::toLowerCase)
//                    .orElse(null);
//
//            if (causeMessage != null) {
//                if (causeMessage.contains("identifier_dup")) {
//                    return new ServiceConflictException(ErrorCode.IDENTIFIER_ALREADY_EXISTS, "Identifier already exists");
//                }
//            }
//
//            return (ConstraintViolationException) e;
//        }
//
//        return mapExceptions(e.getCause());
//    }
//}
