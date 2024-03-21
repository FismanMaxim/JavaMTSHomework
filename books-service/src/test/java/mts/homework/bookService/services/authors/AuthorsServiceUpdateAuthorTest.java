package mts.homework.bookService.services.authors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.services.AuthorUpdateDto;
import mts.homework.bookService.services.AuthorsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import({AuthorsService.class})
public class AuthorsServiceUpdateAuthorTest extends DatabaseSuite {
  @Autowired private JpaAuthorsRepository jpaAuthorsRepository;

  @Autowired private AuthorsService authorsService;

  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    testAuthor = jpaAuthorsRepository.save(new Author("Test", "Author"));
  }

  @Test
  public void testUpdateAuthor() {
    var authorUpdateDto = new AuthorUpdateDto(Optional.of("Updated"), Optional.of("Great Author"));
    Optional<Author> author = authorsService.updateAuthor(testAuthor.getId(), authorUpdateDto);

    assertTrue(author.isPresent());
    assertEquals("Updated", author.get().getFirstName());
    assertEquals("Great Author", author.get().getLastName());
  }

  @Test
  public void testUpdateAuthorFirstNameEmpty() {
    var authorUpdateDto = new AuthorUpdateDto(Optional.empty(), Optional.of("Great Author"));
    Optional<Author> author = authorsService.updateAuthor(testAuthor.getId(), authorUpdateDto);

    assertTrue(author.isPresent());
    assertEquals("Test", author.get().getFirstName());
    assertEquals("Great Author", author.get().getLastName());
  }

  @Test
  public void testUpdateAuthorLastNameEmpty() {
    var authorUpdateDto = new AuthorUpdateDto(Optional.of("Updated"), Optional.empty());
    Optional<Author> author = authorsService.updateAuthor(testAuthor.getId(), authorUpdateDto);

    assertTrue(author.isPresent());
    assertEquals("Updated", author.get().getFirstName());
    assertEquals("Author", author.get().getLastName());
  }
}
