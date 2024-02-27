package starlingtechchallenge.service;

import org.springframework.stereotype.Service;
import starlingtechchallenge.domain.Account;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.domain.request.SavingsGoalRequest;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.SavingsGoalGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;
import starlingtechchallenge.utils.CalculateRoundUp;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RoundUpService {

    private final AccountGateway accountGateway;
    private final SavingsGoalGateway savingsGoalGateway;
    private final TransactionFeedGateway transactionFeedGateway;
    private final CalculateRoundUp calculateRoundUp;

    public RoundUpService(AccountGateway accountGateway,
        SavingsGoalGateway savingsGoalGateway,
        TransactionFeedGateway transactionFeedGateway,
        CalculateRoundUp calculateRoundUp) {
        this.accountGateway = accountGateway;
        this.savingsGoalGateway = savingsGoalGateway;
        this.transactionFeedGateway = transactionFeedGateway;
        this.calculateRoundUp = calculateRoundUp;
    }

    /**
     * Checks if there are any accounts existing. If true, perform round up for out going transactions
     *
     * @param accountUid   account id
     * @param dateTimeFrom start date of query
     * @param dateTimeTo   end date of query
     * @return List of transactions for rounded up transactions
     */
    public AllSavingsGoalDetails calculateRoundUp(final String accountUid, OffsetDateTime dateTimeFrom, OffsetDateTime dateTimeTo) {
        final Account accounts = accountGateway.retrieveCustomerAccounts();

        if (!accounts.getAccounts().isEmpty()) {
            final String categoryUid = accounts.getAccounts().get(0).getDefaultCategory();
            TransactionFeed transactions = transactionFeedGateway.getTransactionFeed(accountUid, categoryUid, dateTimeFrom, dateTimeTo);
            final Amount amount = calculateRoundUp.roundUp(List.of(transactions));

            return getSavingsGoalDetails(accountUid, transactions, amount);
        }
        return new AllSavingsGoalDetails();
    }

    /**
     * Checks if there is a saving goal in list. If savings goal list is empty create a savings goal.
     *
     * @param accountUid account id
     * @param amount Request amount for creating a savings goal
     * @param transactions Transaction information for create savings goal request body
     * @return a list savings goals
     */
    private AllSavingsGoalDetails getSavingsGoalDetails(String accountUid, TransactionFeed transactions, Amount amount) {
        SavingsGoalRequest savingsRequest = new SavingsGoalRequest(getCounterPartyName(transactions), amount.getCurrency(), amount);

        AllSavingsGoalDetails getAllGoals = savingsGoalGateway.getAllSavingsGoals(accountUid);

        if (getAllGoals.getSavingsGoalList().isEmpty()) {
            savingsGoalGateway.createSavingsGoal(accountUid, savingsRequest);
        }
        savingsGoalGateway.addSavingsToGoal(accountUid, getAllGoals.getSavingsGoalList().get(0).getSavingsGoalUid(), amount);
        return getAllGoals;
    }

    private String getCounterPartyName(TransactionFeed transaction) {
        return transaction.getFeedItems().stream().findAny().orElseThrow().getCounterPartyName();
    }
}
