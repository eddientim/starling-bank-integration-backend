package starlingtechchallenge.helpers;

import java.util.List;

import starlingtechchallenge.domain.*;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.domain.response.SavingsGoalResponse;

public class DataBuilders {

    private static final String ACCOUNT_UID = "some-account-uid";
    private static final String CATEGORY_UID = "some-category-uid";
    private static final String SAVING_GOAL_UID = "some-saving-goal-uid";

    public static Account accountData() {
        return Account.builder()
                .accounts(List.of(AccountDetails.builder()
                        .accountUid(ACCOUNT_UID)
                        .accountType((AccountType.PRIMARY))
                        .defaultCategory(CATEGORY_UID)
                        .currency("GBP")
                        .name("Joe").build())).build();
    }

    public static TransactionFeed transactionFeedData() {
        return TransactionFeed.builder()
                .feedItems(List.of(Transaction.builder().counterPartyName("Blogs")
                        .sourceAmount(SourceAmount.builder().currency("GBP").minorUnits(75).build())
                        .categoryUid(CATEGORY_UID).direction("OUT").amount(
                                Amount.builder().currency("GBP").minorUnits(25).build()).build())).build();
    }

    public static AddToSavingsGoalResponse addToSavingsGoalData() {
        return AddToSavingsGoalResponse.builder().transferUid("some-transfer-uid").success(true).build();
    }

    public static AllSavingsGoalDetails allSavingsGoalDetailsData() {
        return AllSavingsGoalDetails.builder()
                .savingsGoalList(List.of(AllSavingsGoalDetail.builder()
                        .savingsGoalUid(SAVING_GOAL_UID)
                        .name("Joe").build()))
                .build();
    }
    public static SavingsGoalResponse savingsGoalResponse() {
        return SavingsGoalResponse.builder().savingsGoalUid(SAVING_GOAL_UID).build();

    }
}
