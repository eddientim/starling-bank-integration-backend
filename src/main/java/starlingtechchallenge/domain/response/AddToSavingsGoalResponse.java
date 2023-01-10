package starlingtechchallenge.domain.response;

import java.util.List;
import lombok.Data;
import starlingtechchallenge.domain.AllSavingsGoalDetails;

@Data
public class AddToSavingsGoalResponse {
  private List<AllSavingsGoalDetails> savingsGoalList;

}
