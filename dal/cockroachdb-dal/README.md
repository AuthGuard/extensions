# AuthGuard CockroachDB DAL Extension

Data access implementations for AuthGuard persistence 
and cache interfaces. This extension is built on top of 
Hibernate DAL with CockroachDB JDBC driver.

## Installation
```xml
<!-- for persistence implementation -->
<dependency>
    <groupId>com.nexblocks.authguard</groupId>
    <artifactId>cockroachdb-persistence</artifactId>
    <version>${authguard.version}</version>
</dependency>
```

```xml
<!-- for cache implementation -->
<dependency>
    <groupId>com.nexblocks.authguard</groupId>
    <artifactId>cockroachdb-cache</artifactId>
    <version>${authguard.version}</version>
</dependency>
```

## Configuration
Refer to the documentation of [Hibernate DAL](/AuthGuard/extensions/tree/master/dal/hibernate-dal)
