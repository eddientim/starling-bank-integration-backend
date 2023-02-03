package starlingtechchallenge.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import starlingtechchallenge.exception.NoTransactionFoundException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionFeed {

  private List<Transaction> feedItems;

  public void noTransactions() {
    if (feedItems.isEmpty()) {
      throw new NoTransactionFoundException("No transactions found");
    }
  }
}
