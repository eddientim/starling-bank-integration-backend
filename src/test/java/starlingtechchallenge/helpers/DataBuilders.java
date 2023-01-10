package starlingtechchallenge.helpers;

import java.util.List;
import starlingtechchallenge.domain.Account;
import starlingtechchallenge.domain.AccountDetails;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.Transaction;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;

public class DataBuilders {

  private static final String ACCOUNT_UID = "some-account-uid";
  private static final String CATEGORY_UID = "some-category-uid";

  public static Account getAccountData() {
    return Account.builder()
        .accounts(List.of(AccountDetails.builder().accountUid(ACCOUNT_UID)
            .defaultCategory(CATEGORY_UID)
            .currency("GBP")
            .name("joe").build())).build();
  }

  public static TransactionFeed getTransactionFeedData() {
    return TransactionFeed.builder()
        .feedItems(List.of(Transaction.builder().categoryUid(CATEGORY_UID).direction("OUT").amount(
            Amount.builder().currency("GBP").minorUnits(34).build()).build())).build();
  }

  public static AddToSavingsGoalResponse getAddToSavingsGoalData() {
    return AddToSavingsGoalResponse.builder().transferUid("some-transfer-uid").build();
  }

}
