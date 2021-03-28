# AuthGuard Extensions

A collection of extensions for AuthGuard. The code of AuthGuard itself is available on its own 
[repository](https://github.com/AuthGuard/AuthGuard).

## Extension
The extensions are broken down based on the part of AuthGuard they integrate with:
* **dal**: Data Access Layer implementations
* **emb**: Event Message Bus bridges
* **email**: Email providers

### Data Access
There are currently two supported implementations: one for MongoDB and one for most SQL databases.

#### Mongo DAL
A data access implementation which uses Mongo for persistence, and a complementary module to use it for caching as well.

#### Hibernate DAL
A data access implementation for SQL databases using Hibernate. Includes both persistence and caching implementations.

### Event Message Bridges
Various modules of AuthGuard publish messages to an internal event system. The provided bridges allow those events to 
be pushed an external messaging systems. 

#### Kafka Bridge
A plugin which connected AuthGuard's internal event system to Kafka. It can be configured to publish all or only specific 
events to Kafka for external systems to process.

### Email
If you want AuthGuard to trigger sending of emails itself, you need an email provider implementation. We currently 
provide only one; more to come.

_Note: Those extensions do not handle retries. You'll need to have your own implementation of a retrying strategy on 
top of those implementations to support retries._ 

#### JavaMail
An email provider implementation which uses JavaMail to send emails to users. It supports SMTP, IMAP, and POP3.

### Local Testing Plugins
In addition to the main plugins, few more are provided to make testing easier, which are:
* Memory DAL: A DAL implementation (both persistence and cache which stores things in memory)
* Log EMB: A plugin which prints all events published to any channel in the event system 

