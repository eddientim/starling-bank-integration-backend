package starlingtechchallenge.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import starlingtechchallenge.domain.Amount;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalAmountRequest {

  private Amount amount;

}
