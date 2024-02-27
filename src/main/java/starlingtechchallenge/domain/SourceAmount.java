package starlingtechchallenge.domain;

import lombok.Data;

@Data
public class SourceAmount {

  private String currency;
  private long minorUnits;
}
