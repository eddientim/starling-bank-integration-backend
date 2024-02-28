package starlingtechchallenge.domain.response;

import lombok.Data;

@Data
public class AddToSavingsGoalResponse {

  private String transferUid;
  private boolean success;
}
