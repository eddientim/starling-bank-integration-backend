package starlingtechchallenge.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import starlingtechchallenge.domain.*;
import starlingtechchallenge.domain.request.SavingsGoalRequest;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.domain.response.SavingsGoalResponse;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.SavingsGoalGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;
import static starlingtechchallenge.helpers.DataBuilders.*;

@SpringBootTest
public class RoundUpServiceTest {

    private final String accountUid = "some-account-uid";
    private final String savingsGoalUid = "some-saving-goal-uid";
    private final String defaultCategoryUid = "some-category-uid";
    private final Amount roundUpAmount = Amount.builder().currency("GBP").minorUnits(75).build();

    private final Instant changesSince = Instant.now();

    final Account accountResponse = accountData();

    final TransactionFeed transactionFeedResponse = transactionFeedData();

    final AllSavingsGoalDetails savingsGoalDetails = allSavingsGoalDetailsData();

    final SavingsGoalRequest savingsGoalRequest = SavingsGoalRequest.builder().build();

    final SavingsGoalResponse savingsGoalResponse = savingsGoalResponse();

    @Mock
    private AccountGateway accountGateway;

    @Mock
    private TransactionFeedGateway transactionFeedGateway;

    @Mock
    private SavingsGoalGateway savingsGoalGateway;

    @InjectMocks
    private RoundUpService roundUpService;

    @Test
    public void shouldCalculateRoundUpForOutGoingTransactions() {

        when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountResponse);

        when(transactionFeedGateway.getTransactionFeed(accountUid, defaultCategoryUid,
                String.valueOf(changesSince))).thenReturn(transactionFeedResponse);

        when(savingsGoalGateway.getAllSavingsGoals(accountUid)).thenReturn(savingsGoalDetails);

        when(savingsGoalGateway.createSavingsGoal(accountUid, savingsGoalRequest)).thenReturn(savingsGoalResponse);

        roundUpService.calculateRoundUp(accountUid, String.valueOf(changesSince));

        verify(transactionFeedGateway)
                .getTransactionFeed(accountUid, defaultCategoryUid, String.valueOf(changesSince));

        verify(savingsGoalGateway).addSavingsToGoal(accountUid, savingsGoalUid, roundUpAmount);
    }

    @Test
    public void shouldCalculateRoundUpForMultipleAccounts() {

        AccountDetails account = AccountDetails.builder().defaultCategory(defaultCategoryUid).build();
        Account multipleAccounts = Account.builder().accounts(List.of(account, account)).build();

        when(accountGateway.retrieveCustomerAccounts()).thenReturn(multipleAccounts);

        when(transactionFeedGateway.getTransactionFeed(accountUid, defaultCategoryUid,
                String.valueOf(changesSince))).thenReturn(transactionFeedResponse);

        when(savingsGoalGateway.getAllSavingsGoals(accountUid)).thenReturn(savingsGoalDetails);

        when(savingsGoalGateway.createSavingsGoal(accountUid, savingsGoalRequest)).thenReturn(savingsGoalResponse);

        roundUpService.calculateRoundUp(accountUid, String.valueOf(changesSince));

        verify(transactionFeedGateway)
                .getTransactionFeed(accountUid, defaultCategoryUid, String.valueOf(changesSince));

        verify(savingsGoalGateway).addSavingsToGoal(accountUid, savingsGoalUid, roundUpAmount);
    }

    @Test
    void shouldNotCalculateRoundUpWhenAccountDoesNotExist() {
        Account accountResponse = Account.builder().accounts(emptyList()).build();

        when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountResponse);

        AllSavingsGoalDetails result = roundUpService
                .calculateRoundUp(accountUid, String.valueOf(changesSince));

        verifyNoInteractions(transactionFeedGateway);
        verifyNoInteractions(savingsGoalGateway);

        Assertions.assertNull(result.getSavingsGoalList());
    }
}

