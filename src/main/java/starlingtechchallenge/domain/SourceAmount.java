package starlingtechchallenge.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SourceAmount {

  private String currency;
  private long minorUnits;
}
