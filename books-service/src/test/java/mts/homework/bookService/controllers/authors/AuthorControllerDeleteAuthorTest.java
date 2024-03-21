package mts.homework.bookService.controllers.authors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
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
public class AuthorControllerDeleteAuthorTest extends DatabaseSuite {
  @Autowired private JpaAuthorsRepository authorsRepository;

  @Autowired private TestRestTemplate http;

  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    authorsRepository.deleteAll();

    testAuthor = authorsRepository.save(new Author("Test", "Author"));
  }

  @Test
  public void createAuthor() {
    http.delete("/api/authors/{id}", Map.of("id", testAuthor.getId()));

    assertEquals(0, authorsRepository.findAll().size());
  }
}
