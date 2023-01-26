# Starling API integration Backend
This service retrieves transactions from the starling bank API and adds the remainder for out going 
transactions to the nearest pound. 

### Service requirements 
- Java 11
- Maven

### Environment variables 
The following env variables must be set up before running the test and service. The `bearer_token` can be found by generating a refresh token for the user in sandbox. 
You can set this in your `.zshrc` `.bash_profile` file or export the following into your terminal.

The `sandbox_api` can be found here - https://developer.starlingbank.com/docs
```
export STARLING_URL=<sandbox_api>

export BEARER_TOKEN=<your_bearer_token>
```

### Set up & Build

Environment values must be setup before running the following steps.

Install maven dependencies
- `mvn install` 

The service can be run with
- `mvn spring-boot:run` or you can start the application from `main` class by clicking the play green button. 

Tests can be run with 
- `mvn test`

### Endpoint to invoke
The `accountUid` can be found in the sandbox account user response.

``http://localhost:8080/round-up/account/{accountUid}?dateTimeFrom={dateTimeFrom}&dateTimeTo={dateTimeTo}``

### Architecture
Sequence diagram of service flow


<img width="823" alt="Sequence diagram " src="https://user-images.githubusercontent.com/5974663/212710292-f65097c5-11ee-4331-8ae2-70673a1bc02e.png">

