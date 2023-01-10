package starlingtechchallenge.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static starlingtechchallenge.helpers.DataBuilders.getAccountData;
import static starlingtechchallenge.helpers.DataBuilders.getAddToSavingsGoalData;
import static starlingtechchallenge.helpers.DataBuilders.getTransactionFeedData;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import starlingtechchallenge.domain.Account;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.domain.request.GoalAmountRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.SavingsGoalGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;

@SpringBootTest
@AutoConfigureMockMvc
public class RoundUpControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private static final String ACCOUNT_UID = "some-account-uid";
  private static final String SAVINGS_GOAL_UID = "some-saving-goal-uid";
  private static final String CATEGORY_UID = "some-category-uid";

  @MockBean
  private AccountGateway accountGateway;

  @MockBean
  private TransactionFeedGateway transactionFeedGateway;

  @MockBean
  private SavingsGoalGateway savingsGoalGateway;

  @Test
  public void shouldReturnSuccessfulResponseForRoundUp() throws Exception {

    Account accountDataResponse = getAccountData();

    TransactionFeed transactionFeedResponse = getTransactionFeedData();

    AddToSavingsGoalResponse addSavingsGoalResponse = getAddToSavingsGoalData();

    Instant changesSince = Instant.parse("2023-01-07T12:34:56.000Z");

    when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountDataResponse);
    when(transactionFeedGateway.getTransactionFeed(ACCOUNT_UID, CATEGORY_UID,
        String.valueOf(changesSince))).thenReturn(transactionFeedResponse);

    when(savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVINGS_GOAL_UID, new GoalAmountRequest(
        Amount.builder().currency("GBP").minorUnits(66).build()))).thenReturn(addSavingsGoalResponse);

    mockMvc.perform(get("/round-up/account/" + ACCOUNT_UID + "/goal-id/" + SAVINGS_GOAL_UID + "?changesSince="
            + changesSince)
            .contentType(APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock_token")
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldThrow4xxErrorWhenUrlIsInvalid() throws Exception {

    Instant changesSince = Instant.parse("2023-01-07T12:34:56.000Z");

    when(accountGateway.retrieveCustomerAccounts()).thenThrow(new HttpClientErrorException(
        HttpStatus.NOT_FOUND,"Invalid to request"));

    mockMvc.perform(get("/invalid-url/" + ACCOUNT_UID + "?changesSince=" + changesSince)
            .contentType(APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }
}