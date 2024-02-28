package starlingtechchallenge.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import starlingtechchallenge.domain.Account;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.SavingsGoalGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;
import starlingtechchallenge.utils.CalculateRoundUp;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;
import static starlingtechchallenge.helpers.Fixtures.*;

@SpringBootTest
public class RoundUpServiceTest {

    private final String accountUid = "some-account-uid";
    private final String savingsGoalUid = "some-saving-goal-uid";
    private final String defaultCategoryUid = "some-category-uid";
    private final Amount roundUpAmount = amountFixture();
    private final OffsetDateTime dateTimeFrom = now();
    private final OffsetDateTime dateTimeTo = now();
    private final Account accountResponse = accountFixture();
    private final TransactionFeed transactionFeedResponse = transactionFeedFixture();
    private final AllSavingsGoalDetails savingsGoalDetails = allSavingsGoalDetailsFixture();

    @Mock
    private AccountGateway accountGateway;
    @Mock
    private CalculateRoundUp calculateRoundUp;
    @Mock
    private TransactionFeedGateway transactionFeedGateway;
    @Mock
    private SavingsGoalGateway savingsGoalGateway;

    @InjectMocks
    private RoundUpService roundUpService;

    @Test
    public void shouldCalculateRoundUpForAnAccount() {
        when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountResponse);
        when(transactionFeedGateway.getTransactionFeed(accountUid, defaultCategoryUid, dateTimeFrom, dateTimeTo)).thenReturn(transactionFeedResponse);
        when(savingsGoalGateway.getAllSavingsGoals(accountUid)).thenReturn(savingsGoalDetails);
        when(calculateRoundUp.roundUp(List.of(transactionFeedResponse))).thenReturn(roundUpAmount);

        roundUpService.calculateRoundUp(accountUid, dateTimeFrom, dateTimeTo);

        verify(transactionFeedGateway).getTransactionFeed(accountUid, defaultCategoryUid, dateTimeFrom, dateTimeTo);
        verify(savingsGoalGateway).addSavingsToGoal(accountUid, savingsGoalUid, roundUpAmount);
    }

    @Test
    public void shouldCalculateRoundUpForMultipleAccounts() {
        Account multipleAccounts = accountFixture();

        when(accountGateway.retrieveCustomerAccounts()).thenReturn(multipleAccounts);
        when(transactionFeedGateway.getTransactionFeed(accountUid, defaultCategoryUid, dateTimeFrom, dateTimeTo)).thenReturn(transactionFeedResponse);
        when(savingsGoalGateway.getAllSavingsGoals(accountUid)).thenReturn(savingsGoalDetails);
        when(calculateRoundUp.roundUp(List.of(transactionFeedResponse))).thenReturn(roundUpAmount);

        roundUpService.calculateRoundUp(accountUid, dateTimeFrom, dateTimeTo);

        verify(transactionFeedGateway).getTransactionFeed(accountUid, defaultCategoryUid, dateTimeFrom, dateTimeTo);
        verify(savingsGoalGateway).addSavingsToGoal(accountUid, savingsGoalUid, roundUpAmount);
    }

    @Test
    void shouldNotCalculateRoundUpWhenAccountDoesNotExist() {
        Account emptyAccount = new Account(emptyList());
        when(accountGateway.retrieveCustomerAccounts()).thenReturn(emptyAccount);

        AllSavingsGoalDetails result = roundUpService.calculateRoundUp(accountUid, dateTimeFrom, dateTimeTo);

        verifyNoInteractions(transactionFeedGateway);
        verifyNoInteractions(savingsGoalGateway);
        Assertions.assertNull(result.getSavingsGoalList());
    }
}

