package starlingtechchallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyAndAmount {

  private String currency;
  private int minorUnits;

}
