package starlingtechchallenge.gateway;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.request.SavingsGoalRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.domain.response.SavingsGoalResponse;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static starlingtechchallenge.helpers.DataBuilders.getAddToSavingsGoalData;

@ExtendWith(SpringExtension.class)
@RestClientTest(SavingsGoalGateway.class)
@ActiveProfiles("test")
public class SavingsGoalGatewayTest {

    private static final String ACCOUNT_UID = "some-account-ui";
    private static final String SAVING_GOAL_UID = "some-saving-goal-uid";

    @Autowired
    private SavingsGoalGateway savingsGoalGateway;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private ObjectMapper objectMapper;

    final AddToSavingsGoalResponse expectedResponse = getAddToSavingsGoalData();

    final Amount request = Amount.builder().currency("GBP").minorUnits(23).build();

    @Test
    public void shouldReturnSuccessfulResponseWhenAddingSavingToGoal() throws Exception {

        String starlingOperationString = objectMapper.writeValueAsString(expectedResponse);
        mockRestServiceServer.expect(requestTo(any(String.class)))
                .andRespond(withSuccess(starlingOperationString, APPLICATION_JSON));

        AddToSavingsGoalResponse actualResponse = savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, request);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldThrow4xxErrorForWhenAddingSavingsToGoal() {
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withBadRequest());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, request));

        Assertions.assertEquals(exception.getStatusCode(), INTERNAL_SERVER_ERROR);
        Assertions.assertEquals("500 Unable to perform action due to server error", exception.getMessage());
    }


    @Test
    void shouldThrow5xxErrorForWhenAddingSavingsToGoal() {
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withServerError());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, request));


        Assertions.assertEquals(exception.getStatusCode(), NOT_FOUND);
        Assertions.assertEquals("404 Invalid url does not exist", exception.getMessage());
    }
}
