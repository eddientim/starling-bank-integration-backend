package starlingtechchallenge.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import starlingtechchallenge.domain.Account;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.GET;

@Component
public class AccountGateway {

    private final String baseurl;
    private final String bearerToken;
    private final RestTemplate restTemplate;

    public AccountGateway(
            @Value("${endpoint.starling-url}") String baseurl,
            @Value("${starling.bearer-token}") String bearerToken,
            final RestTemplateBuilder restTemplateBuilder) {
        this.baseurl = baseurl;
        this.bearerToken = bearerToken;
        restTemplate = restTemplateBuilder.build();
    }

    public Account retrieveCustomerAccounts() throws RestClientException {

        final HttpHeaders headers = new HttpHeaders();
        headers.add(ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, format("Bearer %s", bearerToken));
        final HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseurl + "/api/v2/accounts", GET, httpEntity, Account.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve account info");
        } catch (ResourceAccessException ex) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid request made");
        } catch (Exception ex) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Invalid url does not exist");
        }
    }
}
