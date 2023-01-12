# Edward Ntim Starling Tech Challenge

### Service requirements 
- Java 11

### Environment variables 
The following env variables need to be setup. The `bearer_token` can be found by generating a refresh token user in sandbox. 
You can set this in your `.zshrc` `.bash_profile` file or create a `.env ` file and include the variables in there.

The `sandbox_api` can be found here - https://developer.starlingbank.com/docs
```
export STARLING_URL=<sandbox_api>

export BEARER_TOKEN=<your_bearer_token>
```
For .env file variable setup

``` 
STARLING_URL=<sandbox_api>

BEARER_TOKEN=<your_bearer_token>
```

### Set up & Build

Install maven dependencies
- `mvn install` or you can start the application from `main` class by clicking the play green button. 

The service can be run with
- `mvn run`

Tests can be run with 
- `mvn test`

### Endpoint to invoke
I opted to handle the `within a given week` feature by adding the `changesSince` query parameter in url. The `accountUid` can be found in the sandbox account user response.

``http://localhost:8080/round-up/account/{accountUid}/goal-id/{savingsGoalUid}?changesSince={changesSince}``

A savings goal must to be created hence the `savingsGoalUid` within the path params.  

### Architecture
Sequence diagram of service flow

![Sequence diagram](src/main/architecture/Sequence%20diagram%20.png)

### Further improvements 

I'm aware the `savingGoalUId` can be retrieved from the `getAllSavingsGoals` API to create a savings goal if one doesn't exist. I'm happy to discuss and walk through the implementation of this during the code review. 

For proof of concept I hard coded the `currency` type to be `GBP`. Ideally this should be an enum type with different currency codes. In addition, currency conversion e.g. 
If the user has made transactions in two different countries the conversion rate can be applied to the round up. 

I would include logs for better visibility when the service is running.

Happy to have a discussion about service design and different approaches to the task.
