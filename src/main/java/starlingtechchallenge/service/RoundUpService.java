package starlingtechchallenge.service;

import java.util.List;
import org.springframework.stereotype.Service;
import starlingtechchallenge.NoAccountException;
import starlingtechchallenge.domain.Account;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.domain.request.GoalAmountRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.SavingsGoalGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;

@Service
public class RoundUpService {

  private final AccountGateway accountGateway;
  private final SavingsGoalGateway savingsGoalGateway;

  private final TransactionFeedGateway transactionFeedGateway;

  public RoundUpService(AccountGateway accountGateway, SavingsGoalGateway savingsGoalGateway, TransactionFeedGateway transactionFeedGateway) {
    this.accountGateway = accountGateway;
    this.savingsGoalGateway = savingsGoalGateway;
    this.transactionFeedGateway = transactionFeedGateway;
  }

  public AddToSavingsGoalResponse calculateRoundUp(final String accountUid, final String savingsGoalUid, final String changesSince) {
    final Account accounts = accountGateway.retrieveCustomerAccounts();

    if (!accounts.getAccounts().isEmpty()) {
      final String categoryUid = accounts.getAccounts().get(0).getDefaultCategory();
      TransactionFeed transactions = transactionFeedGateway.getTransactionFeed(accountUid, categoryUid, changesSince);
      final Amount amount = calculateRoundUpForOutGoingTransactions(List.of(transactions));
      GoalAmountRequest request = GoalAmountRequest.builder().amount(amount).build();

      return savingsGoalGateway.addSavingsToGoal(accountUid, savingsGoalUid, request);
    }

    return AddToSavingsGoalResponse.builder().build();
  }

  private Amount calculateRoundUpForOutGoingTransactions(List<TransactionFeed> transactions) {
    final int sum = transactions.stream()
        .filter(item -> item.getFeedItems().get(0).getDirection().equals("OUT"))
        .mapToInt(item -> item.getFeedItems().get(0).getAmount().getMinorUnits())
        .filter(amount -> amount >= 0)
        .map(amount -> 100 - amount % 100)
        .filter(amount -> amount != 100)
        .sum();
    final String currency = transactions.get(0).getFeedItems().get(0).getAmount().getCurrency();
    return Amount.builder().currency(currency).minorUnits(sum).build();
  }
}
