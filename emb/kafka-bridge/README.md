# AuthGuard SendGrid Extension

An extension which publishes messages from AuthGuard's 
internal event message bus to Kafka topics.

## Installation
```xml
<dependency>
    <groupId>com.nexblocks.authguard</groupId>
    <artifactId>kafka-bridge</artifactId>
    <version>${authguard.version}</version>
</dependency>
```

## Configuration

```yaml
kafka:
    clientId: <the ID of the client>
    bootstrapHosts: <a list of Kafka bootstrap hosts>
    topics: <key-value pairs mapping internal channel names to Kafka topic names (example below)>
      otp: authguard.otp
      passwordless: authguard.other
      auth: authguard.other
    producerConfig: <key-value pairs of Apache Kafka producer properties and their values> 
```
