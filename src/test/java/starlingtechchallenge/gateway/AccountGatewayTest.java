package starlingtechchallenge.gateway;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static starlingtechchallenge.helpers.DataBuilders.getAccountData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import starlingtechchallenge.domain.Account;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountGatewayTest {
  private static final String ACCOUNT_UID = "some-account-ui";
  private static final String BASE_URL = "http://localhost/api/v2/accounts";

  private static final String BEARER = "mock-bearer";

  @MockBean
  private AccountGateway accountGateway;
  @Autowired
  MockMvc mockMvc;


  @Test
  public void shouldReturnSuccessfulResponseWhenRetrievingAccounts() throws Exception {

    Account accountDataResponse = getAccountData();

    when(accountGateway.retrieveCustomerAccounts()).thenReturn(accountDataResponse);

    mockMvc.perform(get(BASE_URL)
            .contentType(APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, BEARER)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk());

  }
  @Test
  public void shouldThrow4xxErrorWhenAccountUrlIsInvalid() throws Exception {
    when(accountGateway.retrieveCustomerAccounts()).thenThrow(new HttpClientErrorException(
        HttpStatus.BAD_REQUEST, "Invalid request made"));

    mockMvc.perform(get(BASE_URL)
            .contentType(APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, BEARER)
            .accept(APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

}
