package starlingtechchallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {

  private String accountUid;
  private AccountType accountType;
  private String defaultCategory;
  private String currency;
  private String createdAt;
  private String name;

}
