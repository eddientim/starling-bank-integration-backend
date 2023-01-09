package starlingtechchallenge.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;
import starlingtechchallenge.domain.TransactionFeed;

@Component
public class TransactionFeedGateway {

  private final ObjectMapper objectMapper;
  private final WebClient webClient;
  private final String baseurl;
  private final String bearerToken;

  @Autowired
  public TransactionFeedGateway(final ObjectMapper objectMapper,
      final WebClient webClient,
      @Value("${endpoint.starling-url}") final String baseurl,
      @Value("${starling.bearer-token}") final String bearerToken) {
    this.objectMapper = objectMapper;
    this.webClient = webClient;
    this.baseurl = baseurl;
    this.bearerToken = bearerToken;
  }

  public TransactionFeed getTransactionFeed(final String accountUid, final String categoryUid, final String changesSince) {
    try {
      return webClient.get()
          .uri(baseurl + "/api/v2/feed/account/{accountUid}/category/{categoryUid}?changesSince={changesSince}",
              accountUid, categoryUid, changesSince)
          .accept(MediaType.APPLICATION_JSON)
          .headers(headers -> headers.setBearerAuth(bearerToken))
          .retrieve()
          .bodyToMono(String.class)
          .map(this::transformToResponse)
          .block();
    } catch (HttpClientErrorException ex) {
      throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve transactions info");
    } catch (ResourceAccessException ex) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid request made");
    } catch (Exception ex) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Invalid url does not exist");
    }
  }

  protected TransactionFeed transformToResponse(final String responseBody) {
    try {
      objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      return objectMapper.readValue(responseBody, TransactionFeed.class);
    } catch (JsonProcessingException e) {
      e.getMessage();
    }
    return null;
  }
}