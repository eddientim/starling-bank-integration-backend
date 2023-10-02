package starlingtechchallenge.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.Transaction;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.exception.NoTransactionFoundException;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;
import starlingtechchallenge.service.RoundUpService;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static starlingtechchallenge.helpers.DataBuilders.accountData;
import static starlingtechchallenge.helpers.DataBuilders.transactionFeedData;

@SpringBootTest
public class CalculateRoundUpTest {

  private static final String ACCOUNT_ID = "someAccountId";
  private final static String DEFAULT_CATEGORY_UID = "some-category-uid";
  private final static OffsetDateTime OFFSET_DATE_TIME = now();
  private final static OffsetDateTime DATE_TIME_TO = now();
  @Mock
  private RoundUpService roundUpService;
  @Mock
  private AccountGateway accountGateway;
  @Mock
  private TransactionFeedGateway transactionFeedGateway;

  @InjectMocks
  private CalculateRoundUp calculateRoundUp;

  @Test
  public void shouldCalculateRoundUp() {
    Amount amount = Amount.builder().currency("GBP").minorUnits(10).build();
    Transaction feedItem = Transaction.builder().direction("OUT").amount(amount).build();
    TransactionFeed feedItems = TransactionFeed.builder().feedItems(List.of(feedItem)).build();
    Amount expectedAmount = Amount.builder().currency("GBP").minorUnits(90).build();

    AllSavingsGoalDetails allSavingsGoalDetails = new AllSavingsGoalDetails();

    when(roundUpService.calculateRoundUp(ACCOUNT_ID, OffsetDateTime.now(), OffsetDateTime.now())).thenReturn(allSavingsGoalDetails);
    when(transactionFeedGateway.getTransactionFeed(ACCOUNT_ID, DEFAULT_CATEGORY_UID, OFFSET_DATE_TIME, DATE_TIME_TO)).thenReturn(transactionFeedData());
    when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountData());

    Amount actualAmount = calculateRoundUp.roundUp(feedItems);

    assertEquals(expectedAmount, actualAmount);
  }

  @Test
  public void shouldCalculateMultipleTransactions() {

    int expectedAmountTransaction = 90;
    int expectedAmountTransaction1 = 80;

    Transaction outBoundTransaction = Transaction.builder().direction("OUT")
        .amount(Amount.builder().minorUnits(10).build()).build();

    Transaction outBoundTransaction1 = Transaction.builder().direction("OUT")
            .amount(Amount.builder().minorUnits(20).build()).build();

    Transaction inBoundTransaction = Transaction.builder().direction("IN").build();
    Transaction directDebitTransaction = Transaction.builder().direction("DIRECT_DEBIT").build();

    TransactionFeed transactionFeed = new TransactionFeed(List.of(outBoundTransaction, outBoundTransaction1, inBoundTransaction, directDebitTransaction));

    Amount actualAmount = calculateRoundUp.roundUp(transactionFeed);

    Assertions.assertEquals(expectedAmountTransaction, actualAmount.getMinorUnits());
    Assertions.assertEquals(expectedAmountTransaction1, actualAmount.getMinorUnits());
  }

  @Test
  public void shouldFilterOutInBoundTransactions() {

    Transaction outBoundTransaction = Transaction.builder().direction("OUT").amount(Amount.builder().minorUnits(10).build()).build();
    Transaction outBoundTransaction1 = Transaction.builder().direction("OUT").amount(Amount.builder().minorUnits(10).build()).build();
    Transaction inBoundTransaction = Transaction.builder().direction("IN").build();
    Transaction directDebitTransaction = Transaction.builder().direction("DIRECT_DEBIT").build();

    TransactionFeed transactions = new TransactionFeed(List.of(outBoundTransaction, outBoundTransaction1, inBoundTransaction, directDebitTransaction));

    calculateRoundUp.roundUp(transactions);

    assertEquals(2, transactions.getFeedItems().size());
  }


  @Test
  public void shouldThrowExceptionWhenTransactionListIsEmpty() {
    TransactionFeed emptyFeedItem = TransactionFeed.builder().feedItems(emptyList()).build();

    Assertions.assertThrows(NoTransactionFoundException.class,
        () -> calculateRoundUp.roundUp(emptyFeedItem));
  }
}
