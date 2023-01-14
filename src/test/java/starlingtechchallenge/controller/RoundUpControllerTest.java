package starlingtechchallenge.controller;

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
import starlingtechchallenge.domain.request.SavingsGoalRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.domain.response.SavingsGoalResponse;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.SavingsGoalGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;

import java.time.OffsetDateTime;

import static java.time.OffsetDateTime.now;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static starlingtechchallenge.helpers.DataBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoundUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ACCOUNT_UID = "some-account-uid";
    private static final String SAVINGS_GOAL_UID = "some-saving-goal-uid";
    private static final String CATEGORY_UID = "some-category-uid";
    private final SavingsGoalRequest savingsGoalRequest = SavingsGoalRequest.builder().build();
    private final SavingsGoalResponse savingsGoalResponse = savingsGoalResponse();
    private final AllSavingsGoalDetails savingsGoalDetails = allSavingsGoalDetailsData();
    private final Account accountDataResponse = accountData();
    private final TransactionFeed transactionFeedResponse = transactionFeedData();
    private final AddToSavingsGoalResponse addSavingsGoalResponse = addToSavingsGoalData();

    @MockBean
    private AccountGateway accountGateway;

    @MockBean
    private TransactionFeedGateway transactionFeedGateway;

    @MockBean
    private SavingsGoalGateway savingsGoalGateway;

    @Test
    public void shouldReturnSuccessfulResponseForRoundUp() throws Exception {
        OffsetDateTime dateTimeFrom = now();
        OffsetDateTime dateTimeTo = now();

        when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountDataResponse);
        when(transactionFeedGateway.getTransactionFeed(ACCOUNT_UID, CATEGORY_UID, dateTimeFrom, dateTimeTo))
                .thenReturn(transactionFeedResponse);

        when(savingsGoalGateway.getAllSavingsGoals(ACCOUNT_UID)).thenReturn(savingsGoalDetails);

        when(savingsGoalGateway.createSavingsGoal(ACCOUNT_UID, savingsGoalRequest)).thenReturn(savingsGoalResponse);

        when(savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVINGS_GOAL_UID,
                Amount.builder().currency("GBP").minorUnits(66).build())).thenReturn(addSavingsGoalResponse);

        mockMvc.perform(get("/round-up/account/" + ACCOUNT_UID + "?dateTimeFrom=" + dateTimeFrom + "&dateTimeTo=" + dateTimeTo)
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer mock_token")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldThrow4xxErrorWhenUrlIsInvalid() throws Exception {

        when(accountGateway.retrieveCustomerAccounts()).thenThrow(new HttpClientErrorException(
                HttpStatus.NOT_FOUND, "Invalid to request"));

        mockMvc.perform(get("/invalid-url/" + ACCOUNT_UID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
