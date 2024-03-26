package mts.homework.bookService.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
  @Bean
  public RestTemplate restTemplate(
      @Value("${authors-registry.service.base.url}") String baseUrl,
      @Value("${authors-registry.service.timeout}") long timeoutMillis) {
    var timeout = Duration.ofMillis(timeoutMillis);

    return new RestTemplateBuilder()
        .setConnectTimeout(timeout)
        .setReadTimeout(timeout)
        .rootUri(baseUrl)
        .build();
  }
}
