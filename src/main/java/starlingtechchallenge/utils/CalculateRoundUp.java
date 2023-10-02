package starlingtechchallenge.utils;

import org.springframework.stereotype.Service;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.Transaction;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.exception.NoTransactionFoundException;

import java.util.List;

@Service
public class CalculateRoundUp {

  /**
   * Retrieves a list of transactions out going transactions and calculates savings pot
   * functionality.
   *
   * @param transactions A list of transactions of the account holder
   * @return The remainder value of the rounded number to the nearest upper Integer
   */
  public Amount roundUp(TransactionFeed transactions) throws NoTransactionFoundException {

    transactions.noTransactions();

    if (filterOutGoingTransactions(transactions)) {
    final int sum = transactions.getFeedItems().stream()
        .mapToInt(item -> item.getAmount().getMinorUnits())
        .filter(amount -> amount >= 0)
        .map(amount -> 100 - amount % 100)
        .filter(amount -> amount != 100)
        .sum();
    final String currency = getCurrency(transactions.getFeedItems());
    return Amount.builder().currency(currency).minorUnits(sum).build();
    }
    return Amount.builder().build();
  }

  private String getCurrency(List<Transaction> transactions) {
    return transactions.stream()
            .findAny()
            .orElseThrow()
            .getAmount()
            .getCurrency();
  }
  private boolean filterOutGoingTransactions(TransactionFeed feedItems) {
     return feedItems.getFeedItems().stream().anyMatch(tr -> tr.getDirection().equalsIgnoreCase("OUT"));
  }
}
