#!/bin/sh

# Due to an issue with GitHub Packages, we deploy each one individually

mvn -f dal/hibernate-dal/hibernate-common deploy -DskipTests
mvn -f dal/hibernate-dal/hibernate-cache deploy -DskipTests
mvn -f dal/hibernate-dal/hibernate-persistence deploy -DskipTests

mvn -f dal/memory-dal deploy -DskipTests

mvn -f dal/mongo-dal/mongo-common deploy -DskipTests
mvn -f dal/mongo-dal/mongo-cache deploy -DskipTests
mvn -f dal/mongo-dal/mongo-persistence deploy -DskipTests

mvn -f dal/mysql-dal/mysql-cache deploy -DskipTests
mvn -f dal/mysql-dal/mysql-persistence deploy -DskipTests

mvn -f dal/postgres-dal/postgres-cache deploy -DskipTests
mvn -f dal/postgres-dal/postgres-persistence deploy -DskipTests

mvn -f dal/cockroachdb-dal/cockroachdb-cache deploy -DskipTests
mvn -f dal/cockroachdb-dal/cockroachdb-persistence deploy -DskipTests

mvn -f dal/redis-cache deploy -DskipTests

mvn -f emb/kafka-bridge deploy -DskipTests
mvn -f emb/log-emb deploy -DskipTests

mvn -f sms/log-sms deploy -DskipTests