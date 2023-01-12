package starlingtechchallenge.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import starlingtechchallenge.domain.TransactionFeed;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;
import static starlingtechchallenge.utils.StarlingHeaders.getStarlingHeaders;

@Component
public class TransactionFeedGateway {

    private final RestTemplate restTemplate;
    private final String baseurl;

    public TransactionFeedGateway(@Value("${endpoint.starling-url}") final String baseurl, RestTemplateBuilder restTemplateBuilder) {
        this.baseurl = baseurl;
        restTemplate = restTemplateBuilder.build();
    }

    public TransactionFeed getTransactionFeed(final String accountUid, final String categoryUid, final String changesSince) {

        final Map<String, String> urlParams = new HashMap<>();
        urlParams.put("accountUid", accountUid);
        urlParams.put("categoryUid", categoryUid);

        final String url = fromHttpUrl(baseurl + "/api/v2/feed/account/{accountUid}/category/{categoryUid}?changesSince=changesSince")
                .queryParam("changesSince", changesSince)
                .buildAndExpand(urlParams)
                .toUriString();

        try {
            ResponseEntity<TransactionFeed> response = restTemplate.exchange(url, GET, getStarlingHeaders(), TransactionFeed.class);

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
