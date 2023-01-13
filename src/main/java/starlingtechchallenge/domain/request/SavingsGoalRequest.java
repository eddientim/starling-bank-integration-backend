package starlingtechchallenge.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.CurrencyAndAmount;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsGoalRequest {

  private String name;
  private String currency;
  private Amount currencyAndAmount;
}
