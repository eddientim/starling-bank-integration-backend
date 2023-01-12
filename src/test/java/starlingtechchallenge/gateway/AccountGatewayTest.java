package starlingtechchallenge.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import starlingtechchallenge.domain.Account;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static starlingtechchallenge.helpers.DataBuilders.getAccountData;

@ExtendWith(SpringExtension.class)
@RestClientTest(AccountGateway.class)
@ActiveProfiles("test")
public class AccountGatewayTest {

    @Autowired
    private AccountGateway accountGateway;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private ObjectMapper objectMapper;

    final Account accountDataResponse = getAccountData();

    @Test
    void shouldReturnASuccessfulResponseWithBody() throws JsonProcessingException {

        String accountsString = objectMapper.writeValueAsString(accountDataResponse);
        mockRestServiceServer.expect(requestTo(any(String.class)))
                .andRespond(withSuccess(accountsString, APPLICATION_JSON));

        Account response = accountGateway.retrieveCustomerAccounts();

        Assertions.assertEquals(response, accountDataResponse);
    }

    @Test
    void shouldThrowA5xxErrorWhenRetrievingAccountInformation() {
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withBadRequest());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> accountGateway.retrieveCustomerAccounts());

        Assertions.assertEquals(exception.getStatusCode(), INTERNAL_SERVER_ERROR);
        Assertions.assertEquals("500 Unable to retrieve account info", exception.getMessage());
    }

    @Test
    void shouldThrowA4xxErrorWhenRetrievingAccountInformation() {
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withServerError());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> accountGateway.retrieveCustomerAccounts());

        Assertions.assertEquals(exception.getStatusCode(), NOT_FOUND);
        Assertions.assertEquals("404 Invalid url does not exist", exception.getMessage());
    }

}
