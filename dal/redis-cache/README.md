# AuthGuard Redis Cache Extension

The Redis Cache extension provides implementations of AuthGuard's
cache interfaces with Redis as the external cache. 

## Installation
```xml
<dependency>
    <groupId>com.nexblocks.authguard</groupId>
    <artifactId>redis-cache</artifactId>
    <version>${authguard.version}</version>
</dependency>
```

## Configuration
The configuration of this extension is very straightforward; 
you just need to give it a connection string. You can read 
the connection string from environment variables by using 
"env:<name of env variable>" as the value.

```yaml
redis:
    connectionString: redis://<the redis host and port>..etc
```