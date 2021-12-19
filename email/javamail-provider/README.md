# AuthGuard JavaMail Extension

An email provider extension which uses [JavaMail](https://javaee.github.io/javamail/) 
to send emails. JavaMail supports SMTP, IMAP, and POP3, and any 
the extension can be configured to use any of them.

## Installation
```xml
<dependency>
    <groupId>com.nexblocks.authguard</groupId>
    <artifactId>javamail-provider</artifactId>
    <version>${authguard.version}</version>
</dependency>
```

## Configuration
The configuration of this extension is split into two: provider
configuration, and JavaMail configuration. The first is for 
parameters defined by the provider itself, while the latter is 
for standard JavaMail properties which will be passed directly 
to the library.
 
```yaml
mail:
    provider:
      username: <the username to use for authentication (use "env:<variable name>" to use an environemnt variable)>
      password: <the password to use for authentication (use "env:<variable name>" to use an environemnt variable)>
      fromAddress: <the from email address to use>
      fromName: <the from name to use>
      templates: <map internal template names to Handlebar templates (below is an example using 'passwordless')>
        passwordless:
          file: emails/passwordless.hbs
          subject: Passwordless Yay
    javaMail: <key-value pairs of JavaMail properties and their values (below is an example)>
      "mail.smtp.host": localhost
      "mail.smtp.port": 7465
      "mail.debug": true
      "mail.smtp.auth": true
      "mail.smtp.ssl.enable": true
```

For a list of all available JavaMail properties you can refer 
to [JavaMail documentation](https://javaee.github.io/javamail/docs/api/)
