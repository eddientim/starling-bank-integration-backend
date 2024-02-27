package starlingtechchallenge.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import starlingtechchallenge.domain.TransactionFeed;

import java.time.OffsetDateTime;

import static java.time.OffsetDateTime.now;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static starlingtechchallenge.helpers.Fixtures.transactionFeedFixture;

@RestClientTest(TransactionFeedGateway.class)
@ActiveProfiles("test")
class TransactionFeedGatewayIT {

    private final String ACCOUNT_UID = "some-account-uid";
    private final String CATEGORY_UID = "some-category-uid";
    private final OffsetDateTime dateTimeFrom = now();
    private final OffsetDateTime dateTimeTo = now();

    @Autowired
    private TransactionFeedGateway transactionFeedGateway;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void shouldReturnSuccessfulResponseWhenRetrievingTransactions() throws Exception {
        TransactionFeed transactionFeed = transactionFeedFixture();
        String feedItemsString = OBJECT_MAPPER.writeValueAsString(transactionFeed);
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withSuccess(feedItemsString, APPLICATION_JSON));

        TransactionFeed actualResponse = transactionFeedGateway.getTransactionFeed(ACCOUNT_UID, CATEGORY_UID, dateTimeFrom, dateTimeTo);

        Assertions.assertEquals(transactionFeed, actualResponse);
    }

    @Test
    public void shouldThrow5xxErrorWhenRetrievingTransactions() {
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withBadRequest());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> transactionFeedGateway.getTransactionFeed(ACCOUNT_UID, CATEGORY_UID, dateTimeFrom, dateTimeTo));

        Assertions.assertEquals(exception.getStatusCode(), INTERNAL_SERVER_ERROR);
        Assertions.assertEquals("500 Unable to retrieve transactions info", exception.getMessage());
    }

    @Test
    public void shouldThrow4xxErrorWhenRetrievingTransactions() {
        mockRestServiceServer.expect(requestTo(any(String.class))).andRespond(withServerError());

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> transactionFeedGateway.getTransactionFeed(ACCOUNT_UID, CATEGORY_UID, dateTimeFrom, dateTimeTo));

        Assertions.assertEquals(exception.getStatusCode(), NOT_FOUND);
        Assertions.assertEquals("404 Invalid url does not exist", exception.getMessage());
    }
}
