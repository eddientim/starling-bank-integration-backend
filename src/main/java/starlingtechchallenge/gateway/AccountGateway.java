package starlingtechchallenge.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import starlingtechchallenge.domain.Account;

import static org.springframework.http.HttpMethod.GET;
import static starlingtechchallenge.utils.StarlingHeaders.getStarlingHeaders;

@Service
public class AccountGateway {

    private final RestTemplate restTemplate;

    @Value("${endpoint.starling-url}")
    private String baseurl;

    public AccountGateway(final RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public Account retrieveCustomerAccounts() throws RestClientException {
        HttpEntity<HttpHeaders> headers = getStarlingHeaders();

        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseurl + "/api/v2/accounts", GET, headers, Account.class);
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
