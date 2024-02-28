package starlingtechchallenge.utils;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.Transaction;
import starlingtechchallenge.domain.TransactionFeed;
import starlingtechchallenge.exception.NoTransactionFoundException;
import starlingtechchallenge.gateway.AccountGateway;
import starlingtechchallenge.gateway.TransactionFeedGateway;

import java.time.OffsetDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static starlingtechchallenge.helpers.Fixtures.*;

@SpringBootTest
public class CalculateRoundUpTest {

  public static final String ACCOUNT_UID = "some-account-uid";
  private static final String CATEGORY_UID = "some-category-uid";

  @Mock
  private AccountGateway accountGateway;
  @Mock
  private TransactionFeedGateway transactionFeedGateway;
  @InjectMocks
  private CalculateRoundUp calculateRoundUp;

  @Test
  public void shouldCalculateRoundUp() {
    TransactionFeed transactionFeed = transactionFeedFixture();

    when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountFixture());
    when(transactionFeedGateway.getTransactionFeed(ACCOUNT_UID, CATEGORY_UID, OffsetDateTime.now(), OffsetDateTime.now())).thenReturn(transactionFeed);

    Amount actualAmount = calculateRoundUp.roundUp(List.of(transactionFeed));

    assertEquals(75, actualAmount.getMinorUnits());
  }

  @Test
  public void shouldFilterOutInBoundTransactions() {
    Transaction outBoundTransaction = transactionFixture();
    Transaction inBoundTransaction = transactionFixture();
    inBoundTransaction.setDirection("IN_BOUND");

    TransactionFeed transactionFeed = new TransactionFeed(List.of(outBoundTransaction, inBoundTransaction));

    Amount actualAmount = calculateRoundUp.roundUp(List.of(transactionFeed));

    assertEquals(75, actualAmount.getMinorUnits());
  }

  @Test
  public void shouldThrowExceptionWhenTransactionListIsEmpty() {
    assertThrows(NoTransactionFoundException.class, () -> calculateRoundUp.roundUp((emptyList())));
  }
}