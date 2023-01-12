package starlingtechchallenge.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;
import static starlingtechchallenge.utils.StarlingHeaders.getStarlingHeaders;

@Component
public class SavingsGoalGateway {

    private final RestTemplate restTemplate;

    private final String baseurl;

    public SavingsGoalGateway(@Value("${endpoint.starling-url}") final String baseurl,
                              RestTemplateBuilder restTemplateBuilder) {
        this.baseurl = baseurl;
        restTemplate = restTemplateBuilder
                .build();
    }

    public AddToSavingsGoalResponse addSavingsToGoal(final String accountUid, final String savingsGoalUid, final Amount amount) {

        GoalAmountRequest addToSavingsGoalRequest = GoalAmountRequest.builder().amount(amount).build();

        final HttpEntity<GoalAmountRequest> request = new HttpEntity<>(addToSavingsGoalRequest, getStarlingHeaders().getHeaders());

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
