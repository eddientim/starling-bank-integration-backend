package starlingtechchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
import starlingtechchallenge.service.RoundUpService;

import java.time.OffsetDateTime;

import static java.time.OffsetDateTime.now;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static starlingtechchallenge.helpers.DataBuilders.*;

@SpringBootTest
public class RoundUpControllerTest {

    private static final String ACCOUNT_UID = "some-account-uid";
    private static final String SAVINGS_GOAL_UID = "some-saving-goal-uid";
    private static final String CATEGORY_UID = "some-category-uid";
    private final SavingsGoalRequest savingsGoalRequest = SavingsGoalRequest.builder().build();
    private final SavingsGoalResponse savingsGoalResponse = savingsGoalResponse();
    private final AllSavingsGoalDetails savingsGoalDetails = allSavingsGoalDetailsData();
    private final Account accountDataResponse = accountData();
    private final TransactionFeed transactionFeedResponse = transactionFeedData();
    private final AddToSavingsGoalResponse addSavingsGoalResponse = addToSavingsGoalData();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private AccountGateway accountGateway;
    @Mock
    private TransactionFeedGateway transactionFeedGateway;
    @Mock
    private SavingsGoalGateway savingsGoalGateway;
    @Mock
    private RoundUpService roundUpService;

    @InjectMocks
    private RoundUpController roundUpController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(getRoundUpController())
                .setControllerAdvice()
                .build();
    }

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
        when(roundUpService.calculateRoundUp(ACCOUNT_UID, dateTimeFrom, dateTimeTo)).thenReturn(savingsGoalDetails);

        mockMvc.perform(get("/round-up/account/" + ACCOUNT_UID + "?dateTimeFrom=" + dateTimeFrom + "&dateTimeTo=" + dateTimeTo)
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer mock_token")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(OBJECT_MAPPER.writeValueAsString(savingsGoalDetails)));
    }

    @Test
    public void shouldThrow4xxErrorWhenUrlIsInvalid() throws Exception {

        when(accountGateway.retrieveCustomerAccounts()).thenThrow(new HttpClientErrorException(
                HttpStatus.NOT_FOUND, "Invalid to request"));

        mockMvc.perform(get("/invalid-url/" + ACCOUNT_UID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    protected Object getRoundUpController() {
        return roundUpController;
    }
}
