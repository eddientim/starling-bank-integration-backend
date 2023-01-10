package starlingtechchallenge.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddToSavingsGoalResponse {

  private String transferUid;
  private boolean success;

}
