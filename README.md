# Tiny Bank

Simple bank REST API.

From a functional perspective, the following features are implemented:

1. Creation and deactivation of users
2. Ability for users to deposit/withdraw money from their accounts
3. Ability for users to transfer money to another user's account
4. View account balance
5. View transaction history

From a technical perspective:

1. Functional web application that can be run locally.
2. The application uses in memory storage.

## API Reference

Current implementation manages two domains: user and account. Each user may have only one account (for simplicity’s
sake) and both user and their account are identified by email. That is, even in case of transfer only emails are needed.
To start making operations with account, each user must create user profile first, and account second. This was done to
avoid potential problems when creating both atomically.

### User API

Current implementation has two endpoints, allowing user creation and deactivation (but no reactivation).

1. `/api/user/create` - register user in the system, using their email as primary identifier. If email is already used
   or invalid, returns specific message (even for deactivated user)
2. `/api/user/deactivate` - deactivates user record, but does not delete it. All subsequent operations with user account
   are declined

### Account API

Current implementation has six endpoints, allowing account management, getting current state, and moving funds.

1. `/api/account/create` - register user account in the system, using their email as primary identifier. If email is
   already used, returns specific message
2. `/api/account/deposit` - increase account balance, and return current value, or error message if amount is invalid
3. `/api/account/withdraw`- decrease account balance, and return current value, or error message if amount is invalid or
   account has insufficient funds
4. `/api/account/transfer`- withdraw from one account and deposit another one. If amount is invalid or account has
   insufficient funds, returns specific message
5. `/api/account/balance`- returns account balance, or error if user is inactive, or account does not exist
6. `/api/account/history`- returns account history of transactions, **not paginated**, or error if user is inactive, or
   account does not exist

## How to Run

> First, make sure that you have JDK 21 installed, and both JAVA_HOME and runtime java points to the right version.

To build a "fat JAR" one can simply run Maven Wrapper from project root directory:

```
./mvnw clean package
```

After successful build, the JAR ias stored in _target_ folder, and can be run from terminal using

```
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

To stop the app, just use Ctrl+C, as for any other console program.
Another way to launch the app is simply run
[TinyBankApplication](src/main/java/org/eam/tinybank/TinyBankApplication.java) from IDE.

## How to Test

Having application up and running, one can send JSON requests to the service endpoints, because current implementation
has no Swagger or similar tools. The easiest way is to use Postman, Intellij plugin, or Intellij HTTP scratch:

```js
PUT
http://localhost:8080/api/user/create
    Content - Type
:
application / json

{
    "firstname"
:
    "test",
        "lastname"
:
    "test",
        "email"
:
    "test@test.com"
}
```

## Implementation Details

Java version is 21, using Java optionals and lambda functions, records and ConcurrentHashMap as storage. Frameworks and
libraries used are Spring Boot and Web, Spring Test, Lombok. Build tool is Maven, provided as a wrapper instance along
with the code.
Implementation has the following assumptions:

1. By default, uses in-memory H2 storage with transaction support. To run with Postgres as DB, use `postgres` or
   `docker` Spring profiles, depending on running locally or in Docker.
2. There are no complex input validations
3. REST endpoints are not secured, and there are no passwords for users (TODO Spring Security JWT/Oauth)
4. REST endpoint calls are synchronous (TODO Reactor with backpressure test)
5. String messages are hardcoded in place
6. There is no logging (TODO add Lombok log)
7. There are no real integration tests, only MockMVC ones (TODO)
8. History endpoint does not paginate results (TODO add sample web page)
9. There is no Swagger or similar tools to ease testing (TODO add annotations etc)
