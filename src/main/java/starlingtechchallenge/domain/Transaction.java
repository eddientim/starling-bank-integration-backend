package starlingtechchallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
  private String feedItemUid;
  private String categoryUid;
  private Amount amount;
  private SourceAmount sourceAmount;
  private String direction;
  private String counterPartyUid;
  private String counterPartyName;
}
