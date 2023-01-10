package starlingtechchallenge.gateway;


import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static starlingtechchallenge.helpers.DataBuilders.getAddToSavingsGoalData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.request.GoalAmountRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class SavingsGoalGatewayTest {

  private static final String BASE_URL = "http://localhost/api/v2/account/";
  private static final String ACCOUNT_UID = "some-account-ui";
  private static final String SAVING_GOAL_UID = "some-saving-goal-uid";
  private static final String TRANSFER_UID = "some-transfer-uid";
  private static final String BEARER = "mock-bearer";

  @MockBean
  private SavingsGoalGateway savingsGoalGateway;

  @Autowired
  MockMvc mockMvc;

  @Test
  public void shouldReturnSuccessfulResponseWhenRetrievingAccounts() throws Exception {

    AddToSavingsGoalResponse addSavingsGoalResponse = getAddToSavingsGoalData();

    GoalAmountRequest request = GoalAmountRequest.builder()
        .amount(Amount.builder().currency("GBP").minorUnits(23).build()).build();

    when(savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, request)).thenReturn(
        addSavingsGoalResponse);

    mockMvc.perform(
            put(BASE_URL + ACCOUNT_UID + "/savings-goals/" + SAVING_GOAL_UID + "/add-money/" + TRANSFER_UID)
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER)
                .content(String.valueOf(request))
                .accept(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldThrow4xxErrorWhenAccountUrlIsInvalid() throws Exception {

    GoalAmountRequest request = GoalAmountRequest.builder()
        .amount(Amount.builder().currency("GBP").minorUnits(23).build()).build();

    when(savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, request))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid request made"));

    mockMvc.perform(
            put(BASE_URL + ACCOUNT_UID + "/savings-goals/" + SAVING_GOAL_UID + "/add-money/" + TRANSFER_UID)
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER)
                .content(String.valueOf(request))
                .accept(APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void shouldThrow5xxErrorWhenAccountForServerErrors() throws Exception {

    GoalAmountRequest request = GoalAmountRequest.builder()
        .amount(Amount.builder().currency("GBP").minorUnits(23).build()).build();

    when(savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVING_GOAL_UID, request))
        .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to perform action due to server error"));

    mockMvc.perform(
            put(BASE_URL + ACCOUNT_UID + "/savings-goals/" + SAVING_GOAL_UID + "/add-money/" + TRANSFER_UID)
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER)
                .content(String.valueOf(request))
                .accept(APPLICATION_JSON))
        .andExpect(status().is5xxServerError());
  }
}