package mts.homework.bookService.controllers.authors;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.controllers.responses.AuthorApiEntity;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class AuthorControllerFindAuthorTest extends DatabaseSuite {
  @Autowired private JpaAuthorsRepository authorsRepository;

  @Autowired private TestRestTemplate http;

  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    authorsRepository.deleteAll();

    testAuthor = authorsRepository.save(new Author("Test", "Author"));
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
}
