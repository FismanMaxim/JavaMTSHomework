package mts.homework.bookService.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthorsRegistryServiceGateway implements AuthorsRegistryServiceGatewayBase {
  public static final String IS_AUTHOR_WROTE_THIS_BOOK_ROUTE =
      "/api/authors-registry/is-wrote-this-book";
  private final RestTemplate http;

  public AuthorsRegistryServiceGateway(RestTemplate http) {
    this.http = http;
  }

  @RateLimiter(name = "isWrote")
  @CircuitBreaker(name = "isWrote")
  @Retry(name = "isWrote")
  public boolean isAuthorWroteThisBook(
      String firstName, String lastName, String bookName, String uuid) {
    var headers = new HttpHeaders();

    headers.add("X-REQUEST-ID", uuid);

    var result =
        http.exchange(
            IS_AUTHOR_WROTE_THIS_BOOK_ROUTE
                + "?firstName={firstName}&lastName={lastName}&bookName={bookName}",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            IsAuthorWroteThisBookResponse.class,
            Map.of("firstName", firstName, "lastName", lastName, "bookName", bookName));

    if (!result.getStatusCode().is2xxSuccessful()
        || !result.hasBody()
        || result.getBody() == null) {
      throw new RuntimeException();
    }

    return result.getBody().isWrote();
  }

  @Override
  @RateLimiter(name = "isWrote")
  @CircuitBreaker(name = "isWrote")
  public boolean isAuthorWroteThisBook(String firstName, String lastName, String bookName) {
    return isAuthorWroteThisBook(firstName, lastName, bookName, UUID.randomUUID().toString());
  }

  public record IsAuthorWroteThisBookResponse(boolean isWrote) {}
}
