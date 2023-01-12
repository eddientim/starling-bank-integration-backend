package starlingtechchallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class AllSavingsGoalDetail {

  private String savingsGoalUid;
  private String name;
  private TotalSaved totalSaved;

}
