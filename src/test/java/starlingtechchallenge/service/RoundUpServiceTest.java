package starlingtechchallenge.service;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static starlingtechchallenge.helpers.DataBuilders.getAccountData;
import static starlingtechchallenge.helpers.DataBuilders.getTransactionFeedData;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import starlingtechchallenge.domain.Account;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.SourceAmount;
import starlingtechchallenge.domain.Transaction;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.domain.request.GoalAmountRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.SavingsGoalGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;

@SpringBootTest

public class RoundUpServiceTest {

  private final String accountUid = "some-account-uid";
  private final String savingsGoalUid = "some-saving-goal-uid";
  private final String defaultCategoryUid = "some-category-uid";
  private final GoalAmountRequest roundUpAmount = GoalAmountRequest.builder().amount(Amount.builder().currency("GBP").minorUnits(75).build()).build();

  private final Instant changesSince = Instant.now();

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
    Account accountResponse = getAccountData();

    TransactionFeed transactionFeedResponse = getTransactionFeedData();

    when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountResponse);

    when(transactionFeedGateway.getTransactionFeed(accountUid, defaultCategoryUid,
        String.valueOf(changesSince))).thenReturn(transactionFeedResponse);

    roundUpService.calculateRoundUp(accountUid, savingsGoalUid, String.valueOf(changesSince));

    verify(transactionFeedGateway)
        .getTransactionFeed(accountUid, defaultCategoryUid, String.valueOf(changesSince));

    verify(savingsGoalGateway).addSavingsToGoal(accountUid, savingsGoalUid, roundUpAmount);
  }

  @Test
  void shouldNotCalculateRoundUpForZeroAccounts() {
    Account accountResponse = Account.builder().accounts(emptyList()).build();

    when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountResponse);

    AddToSavingsGoalResponse result = roundUpService
        .calculateRoundUp(accountUid, savingsGoalUid, String.valueOf(changesSince));

    verifyNoInteractions(transactionFeedGateway);
    verifyNoInteractions(savingsGoalGateway);

    Assertions.assertFalse(result.isSuccess());
  }

  @Test
  public void shouldNotCalculateRoundUpIfAmountEqualsZero() {
    Account accountResponse = getAccountData();

    var transactionFeedResponse = TransactionFeed.builder()
        .feedItems(List.of(Transaction.builder()
            .sourceAmount(SourceAmount.builder().currency("GBP").minorUnits(0).build())
            .categoryUid(defaultCategoryUid).direction("OUT").amount(
                Amount.builder().currency("GBP").minorUnits(0).build()).build())).build();

    when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountResponse);

    when(transactionFeedGateway.getTransactionFeed(accountUid, defaultCategoryUid,
        String.valueOf(changesSince))).thenReturn(transactionFeedResponse);

    roundUpService.calculateRoundUp(accountUid, savingsGoalUid, String.valueOf(changesSince));

    verifyNoInteractions(savingsGoalGateway);

  }

  @Test
  public void shouldNotCalculateRoundUpForNonOutGoingTransactions() {
    Account accountResponse = getAccountData();

    when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountResponse);

    var transactionFeedResponse = TransactionFeed.builder()
        .feedItems(List.of(Transaction.builder()
            .sourceAmount(SourceAmount.builder().currency("GBP").minorUnits(75).build())
            .categoryUid(defaultCategoryUid).direction("IN").amount(
                Amount.builder().currency("GBP").minorUnits(25).build()).build())).build();

    AddToSavingsGoalResponse result = roundUpService
        .calculateRoundUp(accountUid, savingsGoalUid, String.valueOf(changesSince));

    when(transactionFeedGateway.getTransactionFeed(accountUid, defaultCategoryUid,
        String.valueOf(changesSince))).thenReturn(transactionFeedResponse);

    verifyNoInteractions(savingsGoalGateway);

    Assertions.assertFalse(result.isSuccess());
  }
}