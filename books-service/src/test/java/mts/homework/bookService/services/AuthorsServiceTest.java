package mts.homework.bookService.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import({AuthorsService.class})
public class AuthorsServiceTest extends DatabaseSuite {
  @Autowired private JpaAuthorsRepository jpaAuthorsRepository;

  @Autowired private AuthorsService authorsService;
  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    jpaAuthorsRepository.deleteAll();
    testAuthor = jpaAuthorsRepository.save(new Author("Test", "Author"));
  }

  @Test
  public void testCreateAuthor() {
    Author authorOpt = authorsService.createNew("Firstname", "Lastname");

    assertEquals("Firstname", authorOpt.getFirstName());
    assertEquals("Lastname", authorOpt.getLastName());
  }

  @Test
  public void testDeleteAuthor() {
    boolean result = authorsService.deleteAuthor(testAuthor.getId());

    assertTrue(result);
  }

  @Test
  public void testFindAuthor() {
    Optional<Author> result = authorsService.findAuthor(testAuthor.getId());

    assertTrue(result.isPresent());
    assertEquals(testAuthor.getId(), result.get().getId());
  }

  @Test
  public void testFindAuthorNoAuthor() {
    Optional<Author> result = authorsService.findAuthor(testAuthor.getId() + 1);

    assertTrue(result.isEmpty());
  }

  @Test
  public void testUpdateAuthor() {
    var authorUpdateDto = new AuthorUpdateDto(Optional.of("Updated"), Optional.of("Great Author"));
    Optional<Author> author = authorsService.updateAuthor(testAuthor.getId(), authorUpdateDto);

    assertTrue(author.isPresent());
    assertEquals("Updated", author.get().getFirstName());
    assertEquals("Great Author", author.get().getLastName());
  }
}
