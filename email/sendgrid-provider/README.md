# AuthGuard SendGrid Extension

An email provider extension which uses SendGrid API to send 
emails. Alternatively, you can also use JavaMail Plugin if 
you prefer to use SendGrid SMTP relay.

## Installation
```xml
<dependency>
    <groupId>com.nexblocks.authguard</groupId>
    <artifactId>sendgrid-provider</artifactId>
    <version>${authguard.version}</version>
</dependency>
```

## Configuration

```yaml
sendgrid:
    apiKeyVariable: <environment variable holding the API key>
    fromName: <the name to use>
    fromEmailAddress: <an email address from a domain which is verified with SendGrid>
    emailTemplates: <key-value pairs mapping internal template name (e.g. 'passwordless') to SendGrid templates> 
```
