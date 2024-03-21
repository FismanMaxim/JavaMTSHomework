package mts.homework.bookService.services.books;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.repositories.DbBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import mts.homework.bookService.exceptions.InvalidBookDataException;
import mts.homework.bookService.services.AuthorsRegistryServiceGatewayBase;
import mts.homework.bookService.services.BookCreationInfo;
import mts.homework.bookService.services.BooksService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import({BooksService.class, DbBooksRepository.class})
class BooksServiceCreateBookTests extends DatabaseSuite {
  @Autowired private JpaAuthorsRepository jpaAuthorsRepository;

  @Autowired private JpaBooksRepository jpaBooksRepository;

  @Autowired private BooksService booksService;

  @MockBean private AuthorsRegistryServiceGatewayBase authorsGateway;

  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    jpaBooksRepository.deleteAll();
    jpaAuthorsRepository.deleteAll();
    testAuthor = jpaAuthorsRepository.save(new Author("Test", "Author"));
  }

  @Test
  public void testSimpleAddition() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);
    BookCreationInfo info =
        new BookCreationInfo(
            testAuthor.getId(), "Экстремальное программирование. Разработка через тестирование");

    assertDoesNotThrow(() -> booksService.createNew(info));
    assertEquals(1, jpaBooksRepository.findAll().size());
  }

  @Test
  public void testAdditionWhenSomethingNull() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);
    BookCreationInfo infoWithNullAuthor =
        new BookCreationInfo(null, "Экстремальное программирование. Разработка через тестирование");
    BookCreationInfo infoWithNullTitle = new BookCreationInfo(testAuthor.getId(), null);

    Assertions.assertThrows(
        InvalidBookDataException.class, () -> booksService.createNew(infoWithNullAuthor));

    Assertions.assertThrows(
        InvalidBookDataException.class, () -> booksService.createNew(infoWithNullTitle));
  }

  @Test
  public void testAdditionAuthorNotFound() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);
    BookCreationInfo info =
        new BookCreationInfo(
            testAuthor.getId() + 1,
            "Экстремальное программирование. Разработка через тестирование");

    assertThrows(InvalidBookDataException.class, () -> booksService.createNew(info));
    assertEquals(0, jpaBooksRepository.findAll().size());
  }

  @Test
  public void testAdditionAuthorNotWroteThisBook() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(false);
    BookCreationInfo info =
        new BookCreationInfo(
            testAuthor.getId(), "Экстремальное программирование. Разработка через тестирование");

    assertThrows(InvalidBookDataException.class, () -> booksService.createNew(info));
  }
}
