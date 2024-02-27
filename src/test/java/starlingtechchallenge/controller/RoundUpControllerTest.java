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
import static starlingtechchallenge.helpers.Fixtures.*;
import static starlingtechchallenge.helpers.Fixtures.allSavingsGoalDetailsFixture;

@SpringBootTest
public class RoundUpControllerTest {

    private static final String ACCOUNT_UID = "some-account-uid";
    private static final String SAVINGS_GOAL_UID = "some-saving-goal-uid";
    private static final String CATEGORY_UID = "some-category-uid";
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
        Account accountDataResponse = accountFixture();
        Amount amount = amountFixture();
        AllSavingsGoalDetails allSavingsGoalDetails = allSavingsGoalDetailsFixture();

        when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountDataResponse);
        when(transactionFeedGateway.getTransactionFeed(ACCOUNT_UID, CATEGORY_UID, dateTimeFrom, dateTimeTo)).thenReturn(transactionFeedFixture());
        when(savingsGoalGateway.getAllSavingsGoals(ACCOUNT_UID)).thenReturn(allSavingsGoalDetails);
        when(savingsGoalGateway.createSavingsGoal(ACCOUNT_UID, savingsGoalRequestFixture())).thenReturn(new SavingsGoalResponse(SAVINGS_GOAL_UID));
        when(savingsGoalGateway.addSavingsToGoal(ACCOUNT_UID, SAVINGS_GOAL_UID, amount)).thenReturn(addToSavingsGoalResponseFixture());
        when(roundUpService.calculateRoundUp(ACCOUNT_UID, dateTimeFrom, dateTimeTo)).thenReturn(allSavingsGoalDetails);

        mockMvc.perform(get("/round-up/account/" + ACCOUNT_UID + "?dateTimeFrom=" + dateTimeFrom + "&dateTimeTo=" + dateTimeTo)
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer mock_token")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(OBJECT_MAPPER.writeValueAsString(allSavingsGoalDetails)));
    }

    @Test
    public void shouldThrow4xxErrorWhenUrlIsInvalid() throws Exception {
        when(accountGateway.retrieveCustomerAccounts()).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Invalid to request"));

        mockMvc.perform(get("/invalid-url/" + ACCOUNT_UID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    protected Object getRoundUpController() {
        return roundUpController;
    }
}
