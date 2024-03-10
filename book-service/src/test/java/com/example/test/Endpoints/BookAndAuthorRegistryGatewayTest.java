package com.example.test.Endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.example.test.Controllers.BookController;
import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.DTOs.BookDTO;
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
    properties = {
      "resilience4j.ratelimiter.instances.createBook.limitForPeriod=1",
      "resilience4j.ratelimiter.instances.createBook.limitRefreshPeriod=1h",
      "resilience4j.ratelimiter.instances.createBook.timeoutDuration=0",
      "resilience4j.circuitbreaker.instances.internalAccessCircuitBreaker.slowCallRateThreshold=1",
      "resilience4j.circuitbreaker.instances.internalAccessCircuitBreaker.slowCallDurationThreshold=1000ms",
      "resilience4j.circuitbreaker.instances.internalAccessCircuitBreaker.slidingWindowType=COUNT_BASED",
      "resilience4j.circuitbreaker.instances.internalAccessCircuitBreaker.slidingWindowSize=1",
      "resilience4j.circuitbreaker.instances.internalAccessCircuitBreaker.minimumNumberOfCalls=1",
      "resilience4j.circuitbreaker.instances.internalAccessCircuitBreaker.waitDurationInOpenState=600s"
    })
@Import(RateLimiterAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookAndAuthorRegistryGatewayTest {
  @Container
  public static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("author-registry.service.base.url", mockServer::getEndpoint);
  }

  @Autowired private BookController bookController;
  @MockBean private RestTemplate restTemplate;

  @Test
  void shouldCreateBook() {
    var client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    client.when(request().withPath("/api/author-registry")).respond(req -> response());

    Book book =
        bookController.createBook(new BookDTO(new Author("John", "Doe"), "someTitle", Set.of()), UUID.randomUUID().toString());

    assertEquals("someTitle", book.getTitle());
  }

  @Test
  void shouldRejectRequestAfterFirstServerSlowResponse() {
    when(restTemplate.postForEntity(eq("/api/author-registry"), any(), eq(boolean.class)))
        .thenAnswer(
            (Answer<ResponseEntity<Boolean>>)
                invocation -> {
                  Thread.sleep(2000);
                  return new ResponseEntity<>(true, HttpStatus.OK);
                });

    assertDoesNotThrow(
        () ->
            bookController.createBook(
                new BookDTO(new Author("John", "Doe"), "someTitle", Set.of()), UUID.randomUUID().toString()));

    assertThrows(
        RestClientException.class,
        () ->
            bookController.createBook(
                new BookDTO(new Author("John", "Doe"), "someTitle", Set.of()), UUID.randomUUID().toString()));
  }

  @Test
  void shouldRejectRequestAfterFirstServerFailResponse() {
    when(restTemplate.postForEntity(eq("/api/author-registry"), any(), eq(boolean.class)))
        .thenThrow(new RestClientException("Unexpected error"));

    assertDoesNotThrow(
        () ->
            bookController.createBook(
                new BookDTO(new Author("John", "Doe"), "someTitle", Set.of()), UUID.randomUUID().toString()));

    assertThrows(
        RestClientException.class,
        () ->
            bookController.createBook(
                new BookDTO(new Author("John", "Doe"), "someTitle", Set.of()), UUID.randomUUID().toString()));
  }
}
