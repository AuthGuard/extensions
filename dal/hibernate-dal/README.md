# AuthGuard Hibernate DAL Extension

The Hibernate DAL (Data Access Layer) extension is a data 
access implementation based on Hibernate. This extension 
itself is not meant to be used directly, but rather through 
another one which includes alongside the driver for a 
particular database. Current variants are:
* [Postgres DAL](/AuthGuard/extensions/tree/master/dal/postgres-dal)
* [MySQL DAL](/AuthGuard/extensions/tree/master/dal/mysql-dal)
* [CockroachDB DAL](/AuthGuard/extensions/tree/master/dal/cockroachdb-dal)

## Installation
Use one of the variants listed above

## Configuration
This extension has no special configuration, its configuration is 
just key-value pairs of standard Hibernate properties which 
will be passed directly to Hibernate sessions. For example, to 
use it with PostgreSQL you can use something like:
```yaml
hibernate:
    hibernate.connection.url: jdbc:postgresql://localhost:5432/postgres
    hibernate.connection.driver_class: org.postgresql.Driver
    hibernate.connection.username: admin
    hibernate.connection.password: mysecretpassword
    hibernate.dialect: org.hibernate.dialect.PostgreSQL9Dialect
```

The dialect and driver will depend on which database you are 
using.
