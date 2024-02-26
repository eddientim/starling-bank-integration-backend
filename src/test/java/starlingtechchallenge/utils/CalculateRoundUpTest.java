package starlingtechchallenge.utils;

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.Transaction;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.exception.NoTransactionFoundException;

@SpringBootTest
public class CalculateRoundUpTest {

  @InjectMocks
  private CalculateRoundUp calculateRoundUp;

  @Test
  public void shouldCalculateRoundUp() {
    Amount amount = Amount.builder().currency("GBP").minorUnits(10).build();
    Transaction feedItem = Transaction.builder().direction("OUT").amount(amount).build();
    TransactionFeed feedItems = TransactionFeed.builder().feedItems(List.of(feedItem))
        .build();

    Amount actualAmount = calculateRoundUp.roundUp(
        Collections.singletonList(feedItems));

    Amount expectedAmount = Amount.builder().currency("GBP").minorUnits(90).build();

    Assertions.assertEquals(expectedAmount, actualAmount);
  }

  @Test
  public void shouldFilterOutInBoundTransactions() {

    Amount expectedAmount = Amount.builder().minorUnits(90).build();

    Transaction outBoundTransaction = Transaction.builder().direction("OUT")
        .amount(Amount.builder().minorUnits(10).build()).build();
    Transaction inBoundTransaction = Transaction.builder().direction("IN").build();
    Transaction directDebitTransaction = Transaction.builder().direction("DIRECT_DEBIT").build();

    TransactionFeed transactionFeed = new TransactionFeed(
        List.of(outBoundTransaction, inBoundTransaction, directDebitTransaction));

    Amount actualAmount = calculateRoundUp.roundUp(
        Collections.singletonList(transactionFeed));

    Assertions.assertEquals(expectedAmount, actualAmount);
  }

  @Test
  public void shouldThrowExceptionWhenTransactionListIsEmpty() {
    TransactionFeed emptyFeedItem = TransactionFeed.builder().feedItems(emptyList()).build();

    Assertions.assertThrows(NoTransactionFoundException.class,
        () -> calculateRoundUp.roundUp(
            Collections.singletonList(emptyFeedItem)));
  }
}