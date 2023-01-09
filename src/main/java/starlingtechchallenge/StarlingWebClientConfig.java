package starlingtechchallenge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class StarlingWebClientConfig {

  @Bean("webClient")
  WebClient webClient() {
    return WebClient.builder().build();
  }
}
