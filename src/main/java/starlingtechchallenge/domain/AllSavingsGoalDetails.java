package starlingtechchallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class AllSavingsGoalDetails {

  private String savingsGoalUid;
  private String name;
  private TotalSaved totalSaved;

}
