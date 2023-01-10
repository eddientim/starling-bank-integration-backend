package starlingtechchallenge.gateway;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static starlingtechchallenge.helpers.DataBuilders.getTransactionFeedData;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import starlingtechchallenge.domain.TransactionFeed;


@SpringBootTest
@AutoConfigureMockMvc
class TransactionFeedGatewayTest {

  private final String BASE_URL = "http://localhost/api/v2/feed/";
  private final String BEARER = "mock-bearer";
  private final String ACCOUNT_UID = "some-account-uid";

  private final String CATEGORY_UID = "some-category-uid";
  @Autowired
  MockMvc mockMvc;

  @MockBean
  private TransactionFeedGateway transactionFeedGateway;

  private final Instant changesSince = Instant.parse("2023-01-07T12:34:56.000Z");

  @Test
  public void shouldReturnSuccessfulResponseWhenRetrievingAccounts() throws Exception {

    TransactionFeed transactionFeedResponse = getTransactionFeedData();

    when(transactionFeedGateway.getTransactionFeed(ACCOUNT_UID,CATEGORY_UID,
        String.valueOf(changesSince))).thenReturn(transactionFeedResponse);

    mockMvc.perform(get(BASE_URL + ACCOUNT_UID +"/category/"+ CATEGORY_UID +"?changesSince=" + changesSince)
            .contentType(APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, BEARER)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldThrow4xxErrorWhenAccountUrlIsInvalid() throws Exception {

    when(transactionFeedGateway.getTransactionFeed(ACCOUNT_UID,CATEGORY_UID,
        String.valueOf(changesSince))).thenThrow(new HttpClientErrorException(
        HttpStatus.BAD_REQUEST, "Invalid request made"));

    mockMvc.perform(get(BASE_URL + ACCOUNT_UID +"/category/"+ CATEGORY_UID +"?changesSince=" + changesSince)
            .contentType(APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, BEARER)
            .accept(APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void shouldThrow5xxErrorWhenAccountForServerErrors() throws Exception {
    when(transactionFeedGateway.getTransactionFeed(ACCOUNT_UID,CATEGORY_UID,
        String.valueOf(changesSince))).thenThrow(new HttpClientErrorException(
        HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve account info"));

    mockMvc.perform(get(BASE_URL + ACCOUNT_UID +"/category/"+ CATEGORY_UID +"?changesSince=" + changesSince)
            .contentType(APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, BEARER)
            .accept(APPLICATION_JSON))
        .andExpect(status().is5xxServerError());
  }
}