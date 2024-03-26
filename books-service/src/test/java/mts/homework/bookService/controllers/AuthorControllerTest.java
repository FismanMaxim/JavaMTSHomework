package mts.homework.bookService.controllers;

import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.TestHelper;
import mts.homework.bookService.controllers.requests.AuthorCreationRequest;
import mts.homework.bookService.controllers.requests.AuthorUpdateRequest;
import mts.homework.bookService.controllers.responses.AuthorApiEntity;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class AuthorControllerTest extends DatabaseSuite {
  @Autowired
  private JpaAuthorsRepository authorsRepository;

  @Autowired private TestRestTemplate http;
  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    authorsRepository.deleteAll();
    testAuthor = authorsRepository.save(new Author("Test", "Author"));
  }

  @Test
  public void createAuthor() {
    var creationRequest = new AuthorCreationRequest("Уилльям", "Шекспир");

    var result = http.postForEntity("/api/authors", creationRequest, AuthorApiEntity.class);

    assertTrue(result.getStatusCode().is2xxSuccessful());
    assertTrue(result.hasBody());

    var body = result.getBody();

    assertNotNull(body);
    assertEquals("Уилльям", body.firstName());
    assertEquals("Шекспир", body.lastName());
    assertEquals(2, authorsRepository.findAll().size());
    assertEquals(authorsRepository.findAll().get(1).getId(), body.id());
  }

  @Test
  public void deleteAuthor() {
    http.delete("/api/authors/{id}", Map.of("id", testAuthor.getId()));

    assertEquals(0, authorsRepository.findAll().size());
  }

  @Test
  public void testFindAuthor() {
    var result =
        http.getForEntity(
            "/api/authors/{id}", AuthorApiEntity.class, Map.of("id", testAuthor.getId()));

    assertTrue(result.getStatusCode().is2xxSuccessful());
    assertTrue(result.hasBody());

    var body = result.getBody();

    assertNotNull(body);
    assertEquals(testAuthor.getId(), body.id());
  }

  @Test
  public void testFindAuthorNotFound() {
    var result =
        http.getForEntity(
            "/api/authors/{id}", AuthorApiEntity.class, Map.of("id", testAuthor.getId() + 1));
    assertTrue(result.getStatusCode().is4xxClientError());
  }

  @Test
  public void updateAuthorTest() {
    var updateRequest = new AuthorUpdateRequest("Александр", "Пушкин");
    var updateRequestEntity = new HttpEntity<>(updateRequest);
    var result =
        http.exchange(
            "/api/authors/{id}",
            HttpMethod.PATCH,
            updateRequestEntity,
            AuthorApiEntity.class,
            Map.of("id", testAuthor.getId()));

    var body = TestHelper.assert2xxAndGetBody(result);

    assertEquals("Александр", body.firstName());
    assertEquals("Пушкин", body.lastName());
  }

  @Test
  public void updateAuthorNotFound() {
    var updateRequest = new AuthorUpdateRequest("Александр", "Пушкин");
    var updateRequestEntity = new HttpEntity<>(updateRequest);
    var result =
        http.exchange(
            "/api/authors/{id}",
            HttpMethod.PATCH,
            updateRequestEntity,
            AuthorApiEntity.class,
            Map.of("id", testAuthor.getId() + 1));

    assertTrue(result.getStatusCode().is4xxClientError());
  }
}
