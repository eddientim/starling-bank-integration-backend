package starlingtechchallenge.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import starlingtechchallenge.domain.Account;
import starlingtechchallenge.domain.AccountDetails;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.SavingsGoalGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;
import static starlingtechchallenge.helpers.DataBuilders.getAccountData;
import static starlingtechchallenge.helpers.DataBuilders.getTransactionFeedData;

@SpringBootTest
public class RoundUpServiceTest {

    private final String accountUid = "some-account-uid";
    private final String savingsGoalUid = "some-saving-goal-uid";
    private final String defaultCategoryUid = "some-category-uid";
    private final Amount roundUpAmount = Amount.builder().currency("GBP").minorUnits(75).build();

    private final Instant changesSince = Instant.now();

    final Account accountResponse = getAccountData();

    final TransactionFeed transactionFeedResponse = getTransactionFeedData();

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

        roundUpService.calculateRoundUp(accountUid, savingsGoalUid, String.valueOf(changesSince));

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

        roundUpService.calculateRoundUp(accountUid, savingsGoalUid, String.valueOf(changesSince));

        verify(transactionFeedGateway)
                .getTransactionFeed(accountUid, defaultCategoryUid, String.valueOf(changesSince));

        verify(savingsGoalGateway).addSavingsToGoal(accountUid, savingsGoalUid, roundUpAmount);
    }

    @Test
    void shouldNotCalculateRoundUpWhenAccountDoesNotExist() {
        Account accountResponse = Account.builder().accounts(emptyList()).build();

        when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountResponse);

        AddToSavingsGoalResponse result = roundUpService
                .calculateRoundUp(accountUid, savingsGoalUid, String.valueOf(changesSince));

        verifyNoInteractions(transactionFeedGateway);
        verifyNoInteractions(savingsGoalGateway);

        Assertions.assertFalse(result.isSuccess());
    }
}
