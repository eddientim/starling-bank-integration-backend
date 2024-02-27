package starlingtechchallenge.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import starlingtechchallenge.domain.Amount;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsGoalRequest {

  private String name;
  private String currency;
  private Amount currencyAndAmount;
}
