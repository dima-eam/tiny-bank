# Tiny Bank

Simple bank REST API.

From a functional perspective, the following features are implemented:

1. Creation and deactivation of users
2. Ability for users to deposit/withdraw money from their accounts
3. Ability for users to transfer money to another user's account
4. View account balances
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

## Implementation Details

Java version is 21, using Java optionals, records and string interpolation. Frameworks and libraries used are Spring
Boot and Web, Spring Test, Lombok. Build tool is Maven, provided as a wrapper instance along with the code.
Implementation has the following assumptions:

1. In-memory storage is custom and based on Java map, and not an embedded database (like H2), thus no transaction
   support, nor atomicity available, only thread safety
2. There are no complex input validations
3. REST endpoints are not secured, and there are no passwords for users
4. REST endpoint calls are synchronous
5. String messages are hardcoded in place
6. There is no logging
7. There are no real integration tests, only MockMVC ones
8. History endpoint does not paginate results
