package starlingtechchallenge.utils;

import org.springframework.stereotype.Service;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.Transaction;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.exception.NoTransactionFoundException;

import java.util.List;
import java.util.function.Supplier;

@Service
public class CalculateRoundUp {

  /**
   * Retrieves a list of transactions out going transactions and calculates savings pot
   * functionality.
   *
   * @param transactions A list of transactions of the account holder
   * @return The remainder value of the rounded number to the nearest upper Integer
   */
  public Amount roundUp(List<TransactionFeed> transactions) throws NoTransactionFoundException {

    if (transactions.isEmpty()) {
      throw new NoTransactionFoundException("No transactions found");
    }

    final int sum = transactions.stream()
        .filter(item -> filterOutDirections(item.getFeedItems()))
        .mapToInt(item -> getUnit(item.getFeedItems()))
        .filter(amount -> amount >= 0)
        .map(amount -> 100 - amount % 100)
        .filter(amount -> amount != 100)
        .sum();
    final String currency = transactions.get(0).getFeedItems().get(0).getAmount().getCurrency();
    return Amount.builder().currency(currency).minorUnits(sum).build();
  }

  private boolean filterOutDirections(List<Transaction> transactions) {
    return transactions.stream().findAny().orElseThrow(getNoTransactionsFound()).getDirection().equals("OUT");
  }

  private int getUnit(List<Transaction> transactions) {
    return transactions.stream().findAny().orElseThrow(getNoTransactionsFound()).getAmount().getMinorUnits();
  }

  private static Supplier<NoTransactionFoundException> getNoTransactionsFound() {
    return () -> new NoTransactionFoundException("No transactions found");
  }
}
