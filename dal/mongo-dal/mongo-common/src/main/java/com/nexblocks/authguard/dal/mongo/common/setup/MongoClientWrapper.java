package com.nexblocks.authguard.dal.mongo.common.setup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.nexblocks.authguard.config.ConfigContext;
import com.nexblocks.authguard.dal.mongo.config.ImmutableMongoConfiguration;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.net.ssl.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

// TODO switch to the reactive client by just changing the import
@Singleton
public class MongoClientWrapper {
    private final MongoClient mongoClient;
    private final ImmutableMongoConfiguration config;

    @Inject
    public MongoClientWrapper(final @Named("mongo") ConfigContext config) {
        this(config.asConfigBean(ImmutableMongoConfiguration.class));
    }

    public MongoClientWrapper(final ImmutableMongoConfiguration config) {
        final CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                new Java8TimeCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(config.getConnectionString()))
                .codecRegistry(pojoCodecRegistry);

        final MongoClientSettings.Builder settings;

        if (config.getUsername() != null) {
            settings = settingsBuilder
                    .applyToSocketSettings(builder -> {
                        builder.connectTimeout(config.getConnectionTimeout(), TimeUnit.MILLISECONDS);
                        builder.readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS);
                    })
                    .applyToClusterSettings( builder -> builder.serverSelectionTimeout(2000, TimeUnit.MILLISECONDS))
                    .credential(MongoCredential.createCredential(config.getUsername(), config.getDatabase(),
                            config.getPassword().toCharArray()));
        } else {
            settings = settingsBuilder
                    .applyToSocketSettings(builder -> {
                        builder.connectTimeout(config.getConnectionTimeout(), TimeUnit.MILLISECONDS);
                        builder.readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS);
                    })
                    .applyToClusterSettings( builder -> builder.serverSelectionTimeout(2000, TimeUnit.MILLISECONDS));
        }

        configureTlsIfNeeded(settings, config);

        this.config = config;
        this.mongoClient = MongoClients.create(settings.build());
    }

    public MongoClient getClient() {
        return this.mongoClient;
    }

    public ImmutableMongoConfiguration getConfig() {
        return this.config;
    }

    private void configureTlsIfNeeded(final MongoClientSettings.Builder settings,
                                      final ImmutableMongoConfiguration config) {
        if (config.getCertificate() != null) {
            final SSLContext sslContext;
            try {
                sslContext = createSslContext(config.getCertificate());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            settings.applyToSslSettings(sslSettings -> sslSettings.context(sslContext));
        }
    }

    private SSLContext createSslContext(final String certificate) throws GeneralSecurityException, IOException {
        // Create a new trust store, use getDefaultType for .jks files or "pkcs12" for .p12 files
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        // Create a new trust store, use getDefaultType for .jks files or "pkcs12" for .p12 files
        trustStore.load(null, null);

        // If you comment out the following, the request will fail
        trustStore.setCertificateEntry(
                "mongo",
                // To test, download the certificate from stackoverflow.com with your browser
                loadCertificate(new File(certificate))
        );
        // Uncomment to following to add the installed certificates to the keystore as well
        //addDefaultRootCaCertificates(trustStore);

        return createSslContext(trustStore);
    }

    private static SSLContext createSslContext(KeyStore trustStore) throws GeneralSecurityException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);

        return sslContext;
    }

    private static SSLSocketFactory createSslSocketFactory(KeyStore trustStore) throws GeneralSecurityException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, null);
        return sslContext.getSocketFactory();
    }

    private static X509Certificate loadCertificate(File certificateFile) throws IOException, CertificateException {
        try (FileInputStream inputStream = new FileInputStream(certificateFile)) {
            return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(inputStream);
        }
    }
}
