package starlingtechchallenge.helpers;

import starlingtechchallenge.domain.*;
import starlingtechchallenge.domain.request.SavingsGoalRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;

import java.util.List;

public class Fixtures {

    private static final String ACCOUNT_UID = "some-account-uid";
    private static final String CATEGORY_UID = "some-category-uid";
    private static final String SAVING_GOAL_UID = "some-saving-goal-uid";
    private static final String TRANSFER_UID = "some-transfer-uid";
    public static final String GBP = "GBP";

    public static Account accountFixture() {
        Account account = new Account();
        account.setAccounts(List.of(accountDetailsFixture()));
        return account;
    }

    public static AccountDetails accountDetailsFixture() {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountUid(ACCOUNT_UID);
        accountDetails.setAccountType(AccountType.PRIMARY);
        accountDetails.setDefaultCategory(CATEGORY_UID);
        accountDetails.setCurrency("GBP");
        accountDetails.setName("Joe");
        return accountDetails;
    }

    public static TransactionFeed transactionFeedFixture() {
        TransactionFeed transactionFeed = new TransactionFeed();
        transactionFeed.setFeedItems(List.of(transactionFixture()));
        return transactionFeed;
    }

    public static Transaction transactionFixture() {
        Transaction transaction = new Transaction();
        transaction.setCounterPartyName("Blogs");
        transaction.setSourceAmount(sourceAmountFixture());
        transaction.setCounterPartyUid(CATEGORY_UID);
        transaction.setDirection("OUT");
        transaction.setAmount(amountFixture());
        return transaction;
    }

    public static SourceAmount sourceAmountFixture() {
        SourceAmount sourceAmount = new SourceAmount();
        sourceAmount.setCurrency(GBP);
        sourceAmount.setMinorUnits(75);
        return sourceAmount;
    }

    public static Amount amountFixture() {
        Amount amount = new Amount();
        amount.setCurrency(GBP);
        amount.setMinorUnits(25);
        return amount;
    }

    public static AllSavingsGoalDetails allSavingsGoalDetailsFixture() {
        AllSavingsGoalDetails allSavingsGoalDetails = new AllSavingsGoalDetails();
        allSavingsGoalDetails.setSavingsGoalList(List.of(allSavingsGoalDetailFixture()));
        return allSavingsGoalDetails;
    }

    public static AllSavingsGoalDetail allSavingsGoalDetailFixture() {
        AllSavingsGoalDetail allSavingsGoalDetail = new AllSavingsGoalDetail();
        allSavingsGoalDetail.setSavingsGoalUid(SAVING_GOAL_UID);
        allSavingsGoalDetail.setName("Joe");
        return allSavingsGoalDetail;
    }

    public static AddToSavingsGoalResponse addToSavingsGoalResponseFixture() {
        AddToSavingsGoalResponse addToSavingsGoalResponse = new AddToSavingsGoalResponse();
        addToSavingsGoalResponse.setSuccess(true);
        addToSavingsGoalResponse.setTransferUid(TRANSFER_UID);
        return addToSavingsGoalResponse;
    }
    public static SavingsGoalRequest savingsGoalRequestFixture() {
        SavingsGoalRequest savingsGoalRequest = new SavingsGoalRequest();
        savingsGoalRequest.setCurrencyAndAmount(amountFixture());
        return savingsGoalRequest;
    }
}
