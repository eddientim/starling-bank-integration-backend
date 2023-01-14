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

import java.time.OffsetDateTime;
import java.util.List;

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

    /**
     * Checks if there are any accounts existing. If true, perform round up for out going transactions
     *
     * @param accountUid account id
     * @param dateTimeFrom start date of query
     * @param dateTimeTo end date of query
     * @return List of transactions for rounded up transactions
     */

    public AllSavingsGoalDetails calculateRoundUp(final String accountUid, OffsetDateTime dateTimeFrom, OffsetDateTime dateTimeTo) {
        final Account accounts = accountGateway.retrieveCustomerAccounts();

        if (!accounts.getAccounts().isEmpty()) {
            final String categoryUid = accounts.getAccounts().get(0).getDefaultCategory();
            TransactionFeed transactions = transactionFeedGateway.getTransactionFeed(accountUid, categoryUid, dateTimeFrom, dateTimeTo);
            final Amount amount = calculateRoundUpForOutGoingTransactions(List.of(transactions));

            AllSavingsGoalDetails getAllGoals = getSavingsGoalDetails(accountUid, amount);

            savingsGoalGateway.addSavingsToGoal(accountUid, getAllGoals.getSavingsGoalList().get(0).getSavingsGoalUid(), amount);
            return getAllGoals;
        }
        return AllSavingsGoalDetails.builder().build();
    }

    /**
     * Checks if there is a saving goal in list. If savings goal list is empty create a savings goal.
     *
     * @param accountUid account id
     * @param amount Request amount for creating a savings goal
     * @return a list savings goals
     */
    private AllSavingsGoalDetails getSavingsGoalDetails(String accountUid, Amount amount) {
        SavingsGoalRequest savingsRequest = SavingsGoalRequest.builder().currencyAndAmount(amount).build();

        AllSavingsGoalDetails getAllGoals = getSavingsGoalDetails(accountUid);

        if (getAllGoals.getSavingsGoalList().isEmpty()) {
            savingsGoalGateway.createSavingsGoal(accountUid, savingsRequest);
        }
        getAllGoals = getSavingsGoalDetails(accountUid);

        return getAllGoals;
    }

    /**
     * Retrieve a list of saving goals from gateway
     *
     * @param accountUid account id
     * @return list of saving goals
     */
    private AllSavingsGoalDetails getSavingsGoalDetails(String accountUid) {
        return savingsGoalGateway.getAllSavingsGoals(accountUid);
    }

    /**
     * Retrieves a list of transactions out going transactions and calculates savings pot functionality.
     *
     * @param transactions A list of transactions of the account holder
     * @return The remainder value of the rounded number to the nearest upper Integer
     */
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
