package starlingtechchallenge.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import starlingtechchallenge.domain.TransactionFeed;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
public class TransactionFeedGateway {

    private final String baseurl;
    private final String bearerToken;
    private final RestTemplate restTemplate;

    public TransactionFeedGateway(
            @Value("${endpoint.starling-url}") String baseurl,
            @Value("${starling.bearer-token}") String bearerToken,
            final RestTemplateBuilder restTemplateBuilder) {
        this.baseurl = baseurl;
        this.bearerToken = bearerToken;
        restTemplate = restTemplateBuilder.build();
    }

    /**
     * GET request to retrieve all transactions.
     * @param accountUid Account id
     * @param categoryUid Category id
     * @param minTransactionTimestamp Start date of query search for savings goal
     * @param maxTransactionTimestamp End date of query search for savings goal
     * @return TransactionFeed
     */

    public TransactionFeed getTransactionFeed(final String accountUid, final String categoryUid,
                                              OffsetDateTime minTransactionTimestamp,
                                              OffsetDateTime maxTransactionTimestamp) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(ACCEPT, APPLICATION_JSON_VALUE);
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, format("Bearer %s", bearerToken));
        final HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        final Map<String, String> urlParams = new HashMap<>();
        urlParams.put("accountUid", accountUid);
        urlParams.put("categoryUid", categoryUid);

        final String url = fromHttpUrl(baseurl + "/api/v2/feed/account/{accountUid}/category/{categoryUid}/transactions-between")
                .queryParam("minTransactionTimestamp", minTransactionTimestamp.toString())
                .queryParam("maxTransactionTimestamp", maxTransactionTimestamp.toString())
                .buildAndExpand(urlParams).toUriString();

        try {
            ResponseEntity<TransactionFeed> response = restTemplate
                    .exchange(url, GET, httpEntity, TransactionFeed.class);

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve transactions info");
        } catch (ResourceAccessException ex) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid request made");
        } catch (Exception ex) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Invalid url does not exist");
        }
    }
}
