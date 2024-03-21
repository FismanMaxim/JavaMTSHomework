package mts.homework.bookService.services.authors.gateway;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(
    classes = {AuthorsRegistryServiceGateway.class},
    properties = {
      "resilience4j.circuitbreaker.instances.isWrote.slowCallRateThreshold=1",
      "resilience4j.circuitbreaker.instances.isWrote.slowCallDurationThreshold=1000ms",
      "resilience4j.circuitbreaker.instances.isWrote.slidingWindowType=COUNT_BASED",
      "resilience4j.circuitbreaker.instances.isWrote.slidingWindowSize=1",
      "resilience4j.circuitbreaker.instances.isWrote.minimumNumberOfCalls=1",
      "resilience4j.circuitbreaker.instances.isWrote.waitDurationInOpenState=600s"
    })
@Import(CircuitBreakerAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthorsServiceGatewayCircuitBreakerTests {
  @Autowired private AuthorsRegistryServiceGateway universityGateway;

  @MockBean private RestTemplate restTemplate;

  @Test
  public void testServerSlowResponse() {
    when(restTemplate.exchange(
            any(String.class),
            any(HttpMethod.class),
            any(HttpEntity.class),
            eq(AuthorsRegistryServiceGateway.IsAuthorWroteThisBookResponse.class),
            any(Map.class)))
        .thenAnswer(
            invocation -> {
              Thread.sleep(1000);
              return new ResponseEntity<>(
                  new AuthorsRegistryServiceGateway.IsAuthorWroteThisBookResponse(true),
                  HttpStatus.OK);
            });

    assertDoesNotThrow(
        () -> universityGateway.isAuthorWroteThisBook("Test", "Author", "Test Book"));
    assertThrows(
        CallNotPermittedException.class,
        () -> universityGateway.isAuthorWroteThisBook("Test", "Author", "Test Book"));
  }

  @Test
  public void testServerFailedResponse() {
    when(restTemplate.exchange(
            any(String.class),
            any(HttpMethod.class),
            any(HttpEntity.class),
            eq(AuthorsRegistryServiceGateway.IsAuthorWroteThisBookResponse.class),
            any(Map.class)))
        .thenThrow(new RestClientException("Test"));

    assertThrows(
        RestClientException.class,
        () -> universityGateway.isAuthorWroteThisBook("Test", "Author", "Test Book"));
    assertThrows(
        CallNotPermittedException.class,
        () -> universityGateway.isAuthorWroteThisBook("Test", "Author", "Test Book"));
  }
}
