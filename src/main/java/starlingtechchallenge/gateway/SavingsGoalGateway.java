package starlingtechchallenge.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import starlingtechchallenge.domain.Amount;
import starlingtechchallenge.domain.request.GoalAmountRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
public class SavingsGoalGateway {

    private final String baseurl;
    private final String bearerToken;
    private final RestTemplate restTemplate;

    public SavingsGoalGateway(
            @Value("${endpoint.starling-url}") String baseurl,
            @Value("${starling.bearer-token}") String bearerToken,
            final RestTemplateBuilder restTemplateBuilder) {
        this.baseurl = baseurl;
        this.bearerToken = bearerToken;
        restTemplate = restTemplateBuilder.build();
    }

    public AddToSavingsGoalResponse addSavingsToGoal(final String accountUid, final String savingsGoalUid, final Amount amount) {

        final HttpHeaders headers = new HttpHeaders();
        headers.add(ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, format("Bearer %s", bearerToken));

        GoalAmountRequest addToSavingsGoalRequest = GoalAmountRequest.builder().amount(amount).build();

        final HttpEntity<GoalAmountRequest> request = new HttpEntity<>(addToSavingsGoalRequest, headers);

        final Map<String, String> urlParams = new HashMap<>();
        urlParams.put("accountUid", accountUid);
        urlParams.put("savingsGoalUid", savingsGoalUid);
        urlParams.put("transferUid", String.valueOf(UUID.randomUUID()));

        final String url = fromHttpUrl(baseurl + "/api/v2/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}")
                .buildAndExpand(urlParams)
                .toUriString();

        try {
            final ResponseEntity<AddToSavingsGoalResponse> response = restTemplate
                    .exchange(url,
                            PUT,
                            request,
                            AddToSavingsGoalResponse.class);

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to perform action due to server error");
        } catch (ResourceAccessException ex) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid amount made");
        } catch (Exception ex) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Invalid url does not exist");
        }
    }
}
