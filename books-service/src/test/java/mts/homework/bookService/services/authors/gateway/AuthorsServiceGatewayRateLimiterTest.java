package mts.homework.bookService.services.authors.gateway;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import java.util.Map;
import mts.homework.bookService.services.AuthorsRegistryServiceGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(
    classes = {AuthorsRegistryServiceGateway.class},
    properties = {
      "resilience4j.ratelimiter.instances.isWrote.limitForPeriod=1",
      "resilience4j.ratelimiter.instances.isWrote.limitRefreshPeriod=1h",
      "resilience4j.ratelimiter.instances.isWrote.timeoutDuration=0"
    })
@Import(RateLimiterAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthorsServiceGatewayRateLimiterTest {
  @Autowired private AuthorsRegistryServiceGateway universityGateway;

  @MockBean private RestTemplate restTemplate;

  @Test
  public void testRateLimiter() {
    when(restTemplate.exchange(
            any(String.class),
            any(HttpMethod.class),
            any(HttpEntity.class),
            eq(AuthorsRegistryServiceGateway.IsAuthorWroteThisBookResponse.class),
            any(Map.class)))
        .thenAnswer(
            invocation ->
                new ResponseEntity<>(
                    new AuthorsRegistryServiceGateway.IsAuthorWroteThisBookResponse(true),
                    HttpStatus.OK));

    assertDoesNotThrow(
        () -> universityGateway.isAuthorWroteThisBook("Test", "Author", "Test Book"));
    assertThrows(
        RequestNotPermitted.class,
        () -> universityGateway.isAuthorWroteThisBook("Test", "Author", "Test Book"));
  }
}
