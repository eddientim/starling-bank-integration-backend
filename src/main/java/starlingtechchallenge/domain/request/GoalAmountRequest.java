package starlingtechchallenge.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import starlingtechchallenge.domain.Amount;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalAmountRequest {

  private Amount amount;

}
