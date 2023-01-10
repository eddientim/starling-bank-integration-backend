package starlingtechchallenge.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;
import starlingtechchallenge.domain.request.GoalAmountRequest;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;

@Component
public class SavingsGoalGateway {

  private final ObjectMapper objectMapper;
  private final WebClient webClient;
  private final String baseurl;
  private final String bearerToken;

  @Autowired
  public SavingsGoalGateway(final ObjectMapper objectMapper,
      final WebClient webClient,
      @Value("${endpoint.starling-url}") final String baseurl,
      @Value("${starling.bearer-token}") final String bearerToken) {
    this.objectMapper = objectMapper;
    this.webClient = webClient;
    this.baseurl = baseurl;
    this.bearerToken = bearerToken;
  }


  public AddToSavingsGoalResponse addSavingsToGoal(final String accountUid, final String savingsGoalUid, final GoalAmountRequest request) {
    try {
      String transferUid = String.valueOf(UUID.randomUUID());
      return webClient.put()
          .uri(baseurl + "/api/v2/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}",
              accountUid, savingsGoalUid, transferUid)
          .accept(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .headers(headers -> headers.setBearerAuth(bearerToken))
          .retrieve()
          .bodyToMono(String.class)
          .map(this::transformToResponseToSavingsGoal)
          .block();
    } catch (HttpClientErrorException ex) {
      throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to perform action due to server error");
    } catch (ResourceAccessException ex) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid request made");
    } catch (Exception ex) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Invalid url does not exist");
    }
  }

  protected AddToSavingsGoalResponse transformToResponseToSavingsGoal(final String responseBody) {
    try {
      objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      return objectMapper.readValue(responseBody, AddToSavingsGoalResponse.class);
    } catch (JsonProcessingException e) {
      e.getMessage();
    }
    return null;
  }
}
