package starlingtechchallenge.service;

import java.math.BigDecimal;
import starlingtechchallenge.domain.Amount;

public interface SumOfRoundUp {

    BigDecimal calculate(Amount amount);

}
