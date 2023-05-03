package starlingtechchallenge.domain;

import lombok.Builder;
import starlingtechchallenge.exception.NoTransactionFoundException;

import java.util.List;

@Builder
public class TransactionFeed {

    private List<Transaction> feedItems;

    public TransactionFeed(List<Transaction> feedItems) {
        this.feedItems = feedItems;
    }

    public void noTransactions() {
        if (feedItems.isEmpty()) {
            throw new NoTransactionFoundException("No transactions found");
        }
    }

    public List<Transaction> getFeedItems() {
        return feedItems;
    }
}
