package mts.homework.bookService.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.DbBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import mts.homework.bookService.exceptions.InvalidBookDataException;
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

import java.util.Optional;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import({BooksService.class, DbBooksRepository.class})
class BooksServiceTest extends DatabaseSuite {
  @Autowired private JpaAuthorsRepository jpaAuthorsRepository;
  @Autowired private JpaBooksRepository jpaBooksRepository;
  @Autowired private BooksService booksService;
  @Autowired private JpaTagsRepository jpaTagsRepository;
  @MockBean private AuthorsRegistryServiceGatewayBase authorsGateway;
  private Book testBook;

  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    jpaBooksRepository.deleteAll();
    jpaAuthorsRepository.deleteAll();

    testAuthor = new Author("Test", "Author");
    jpaAuthorsRepository.save(testAuthor);

    testBook = new Book("Test Book", testAuthor);
    jpaBooksRepository.save(testBook);
  }

  @Test
  public void testSimpleAddition() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);
    BookCreationInfo info =
        new BookCreationInfo(
            testAuthor.getId(), "Экстремальное программирование. Разработка через тестирование");

    assertDoesNotThrow(() -> booksService.createNew(info));
    assertEquals(2, jpaBooksRepository.findAll().size());
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
    assertEquals(1, jpaBooksRepository.findAll().size());
  }

  @Test
  public void testAdditionAuthorNotWroteThisBook() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(false);
    BookCreationInfo info =
        new BookCreationInfo(
            testAuthor.getId(), "Экстремальное программирование. Разработка через тестирование");

    assertThrows(InvalidBookDataException.class, () -> booksService.createNew(info));
  }

  @Test
  public void testSimpleDelete() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);
    assertTrue(booksService.deleteBook(testBook.getId()));
    assertEquals(0, jpaBooksRepository.findAll().size());
  }

  @Test
  public void testDeleteBookNotExists() {
    boolean isDeleted = booksService.deleteBook(testBook.getId() + 1);

    Assertions.assertFalse(isDeleted);
  }

  @Test
  public void testSimpleFind() {
    var result = booksService.findBook(testBook.getId());

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(testBook.getId(), result.get().getId());
  }

  @Test
  public void updateAuthor() {
    var anotherAuthor = new Author("Another", "Author");
    jpaAuthorsRepository.save(anotherAuthor);

    var book = booksService.updateBookAuthor(testBook.getId(), anotherAuthor.getId());

    assertTrue(book.isPresent());
    assertEquals(testBook.getId(), book.get().getId());
    assertEquals(anotherAuthor.getId(), book.get().getAuthor().getId());
  }

  @Test
  public void updateTitle() {
    var book = booksService.updateBookTitle(testBook.getId(), "Another Test Book");

    assertTrue(book.isPresent());
    assertEquals(testBook.getId(), book.get().getId());
    assertEquals("Another Test Book", book.get().getTitle());
  }

  @Test
  public void updateAddTag() {
    var tag = new Tag("Some Test Tag");
    jpaTagsRepository.save(tag);

    Optional<Book> book = booksService.addNewTag(testBook.getId(), tag.getId());

    assertTrue(book.isPresent());
    assertEquals(1, book.get().getTags().size());
  }
}
