# Edward Ntim Starling Tech Challenge

### Service requirements 
- Java 11

### Environment variables 
The following env variables need to be setup. The `bearer_token` can be found by generating a refresh token user in sandbox.

The `Sandbox_api` can be found here - https://developer.starlingbank.com/docs
```
STARLING_BANK_API=<Sandbox_api>`

BEARER_TOKEN=<Your_bearer_token>
```

### Set up & Build

Install maven dependencies
-  ``mvn install`` or you can start the application from `main` class

The service can be run with
- ``mvn run``

Tests can be run with 
- `mvn test`

### Endpoint to invoke
I opted to handle the 'within a given week' feature by adding the `changesSince` query parameter in url. The `accountUid` can be found in the sandbox user response.

``http://localhost:8080/round-up/account/{accountUid}/goal-id/{savingsGoalUid}?changesSince={changesSince}``

