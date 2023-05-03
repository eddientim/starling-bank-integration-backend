package starlingtechchallenge.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.Transaction;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.exception.NoTransactionFoundException;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CalculateRoundUpTest {

  @Autowired
  private CalculateRoundUp calculateRoundUp;

  @Test
  public void shouldCalculateRoundUp() {
    Amount amount = Amount.builder().currency("GBP").minorUnits(10).build();
    Transaction feedItem = Transaction.builder().direction("OUT").amount(amount).build();
    TransactionFeed feedItems = TransactionFeed.builder().feedItems(List.of(feedItem))
        .build();

    Amount actualAmount = calculateRoundUp.roundUp(feedItems);

    Amount expectedAmount = Amount.builder().currency("GBP").minorUnits(90).build();

    Assertions.assertEquals(expectedAmount, actualAmount);
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
