package com.nexblocks.authguard.dal.mongo.persistence;

import com.nexblocks.authguard.dal.mongo.config.ImmutableMongoConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoDbTestContainer {
    public static final DockerImageName MONGO_4_IMAGE = DockerImageName.parse("mysql:8.0.24");

    private static MongoDBContainer container;

    static void start() {
        if (container == null) {
            container = new MongoDBContainer();
        }

        if (!container.isRunning()) {
            container.start();
        }
    }

    static void stop() {
        container.stop();
    }

    static ImmutableMongoConfiguration configuration() {
        return ImmutableMongoConfiguration.builder()
                .connectionString(container.getReplicaSetUrl())
                .database("tests")
                .build();
    }
}
