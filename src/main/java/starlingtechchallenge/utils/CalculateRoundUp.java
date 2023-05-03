package starlingtechchallenge.utils;

import java.util.List;
import org.springframework.stereotype.Service;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.exception.NoTransactionFoundException;

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

    final int sum = transactions.getFeedItems().stream()
        .filter(item -> item.getDirection().equals("OUT"))
        .mapToInt(item -> item.getAmount().getMinorUnits())
        .filter(amount -> amount >= 0)
        .map(amount -> 100 - amount % 100)
        .filter(amount -> amount != 100)
        .sum();
    final String currency = transactions.getFeedItems().get(0).getAmount().getCurrency();
    return Amount.builder().currency(currency).minorUnits(sum).build();
  }
}
