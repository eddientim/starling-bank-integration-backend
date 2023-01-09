package starlingtechchallenge.helpers;

import java.util.List;
import starlingtechchallenge.domain.Account;
import starlingtechchallenge.domain.AccountDetails;

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

}