package starlingtechchallenge.gateway;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.request.SavingsGoalRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.domain.response.SavingsGoalResponse;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static starlingtechchallenge.helpers.Fixtures.*;

@RestClientTest(SavingsGoalGateway.class)
@ActiveProfiles("test")
public class SavingsGoalGatewayIT {

    private final String ACCOUNT_UID = "some-account-ui";
    private final String SAVING_GOAL_UID = "some-saving-goal-uid";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private SavingsGoalGateway savingsGoalGateway;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    final AddToSavingsGoalResponse expectedResponse = addToSavingsGoalResponseFixture();

    @Test
    public void shouldReturnSuccessfulResponseWhenAddingSavingToGoal() throws Exception {
        Amount amount = amountFixture();
        String mapper = OBJECT_MAPPER.writeValueAsString(expectedResponse);
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withSuccess(mapper, APPLICATION_JSON));

        AddToSavingsGoalResponse actualResponse = savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, amount);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldThrow4xxErrorWhenAddingSavingsToGoal() {
        Amount amount = amountFixture();
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withBadRequest());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, amount));

        Assertions.assertEquals(exception.getStatusCode(), INTERNAL_SERVER_ERROR);
        Assertions.assertEquals("500 Unable to perform action due to server error", exception.getMessage());
    }

    @Test
    void shouldThrow5xxErrorWhenAddingSavingsToGoal() {
        Amount amount = amountFixture();

        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withServerError());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, amount));

        Assertions.assertEquals(exception.getStatusCode(), NOT_FOUND);
        Assertions.assertEquals("404 Invalid url does not exist", exception.getMessage());
    }

    @Test
    public void shouldReturnSuccessfulResponseWhenRetrievingAccounts() throws Exception {
        SavingsGoalResponse savingsGoalResponse = new SavingsGoalResponse();
        savingsGoalResponse.setSavingsGoalUid(SAVING_GOAL_UID);

        SavingsGoalRequest request = savingsGoalRequestFixture();

        String mapper = OBJECT_MAPPER.writeValueAsString(savingsGoalResponse);
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withSuccess(mapper, APPLICATION_JSON));

        SavingsGoalResponse actualResponse = savingsGoalGateway.createSavingsGoal(ACCOUNT_UID, request);

        Assertions.assertEquals(savingsGoalResponse, actualResponse);
    }

    @Test
    void shouldThrow4xxErrorWhenCreatingAGoal() {
        SavingsGoalRequest request = savingsGoalRequestFixture();

        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withBadRequest());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> savingsGoalGateway.createSavingsGoal(ACCOUNT_UID, request));

        Assertions.assertEquals(exception.getStatusCode(), INTERNAL_SERVER_ERROR);
        Assertions.assertEquals("500 Unable to perform action due to server error", exception.getMessage());
    }

    @Test
    void shouldThrow5xxErrorWhenCreatingAGoal() {
        SavingsGoalRequest request = savingsGoalRequestFixture();
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withServerError());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> savingsGoalGateway.createSavingsGoal(ACCOUNT_UID, request));


        Assertions.assertEquals(exception.getStatusCode(), NOT_FOUND);
        Assertions.assertEquals("404 Invalid url does not exist", exception.getMessage());
    }

    @Test
    public void shouldReturnSuccessfulResponseWhenRetrievingSavingsGoals() throws Exception {
        AllSavingsGoalDetails expectedResponse = allSavingsGoalDetailsFixture();

        String mapper = OBJECT_MAPPER.writeValueAsString(expectedResponse);
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withSuccess(mapper, APPLICATION_JSON));

        AllSavingsGoalDetails actualResponse = savingsGoalGateway.getAllSavingsGoals(ACCOUNT_UID);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldThrow4xxErrorWhenRetrievingSavingsGoal() {
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withBadRequest());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> savingsGoalGateway.getAllSavingsGoals(ACCOUNT_UID));

        Assertions.assertEquals(exception.getStatusCode(), INTERNAL_SERVER_ERROR);
        Assertions.assertEquals("500 Unable to perform action due to server error", exception.getMessage());
    }

    @Test
    void shouldThrow5xxErrorWhenRetrievingSavingsGoal() {
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withServerError());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> savingsGoalGateway.getAllSavingsGoals(ACCOUNT_UID));

        Assertions.assertEquals(exception.getStatusCode(), NOT_FOUND);
        Assertions.assertEquals("404 Invalid url does not exist", exception.getMessage());
    }
}