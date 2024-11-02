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

## Implementation Details

Java version is 21, using Java optionals, records and string interpolation. Frameworks and libraries used are Spring
Boot and Web, Spring Test, Lombok. Build tool is Maven, provided as a wrapper instance along with the code.
Implementation has the following assumptions:

1. In-memory storage is custom and based on Java map, and not an embedded database (like H2)
2. There are no complex input validations
3. REST endpoints are not secured
4. String messages are hardcoded in place
