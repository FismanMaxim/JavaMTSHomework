package mts.homework.bookService.services.books;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.repositories.DbBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import mts.homework.bookService.services.AuthorsRegistryServiceGatewayBase;
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
@Import({BooksService.class, DbBooksRepository.class, AuthorsRegistryServiceGatewayBase.class})
public class BooksServiceDeleteBookTests extends DatabaseSuite {
  @Autowired private JpaBooksRepository booksRepository;

  @Autowired private JpaAuthorsRepository authorsRepository;

  @Autowired private BooksService booksService;

  @MockBean private AuthorsRegistryServiceGatewayBase authorsRegistry;

  private Book testBook;

  @BeforeEach
  public void setUp() {
    booksRepository.deleteAll();
    authorsRepository.deleteAll();

    var testAuthor = new Author("Test", "Author");
    authorsRepository.save(testAuthor);

    testBook = new Book("Test Book", testAuthor);
    booksRepository.save(testBook);
  }

  @Test
  public void testSimpleDelete() {
    when(authorsRegistry.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);
    assertTrue(booksService.deleteBook(testBook.getId()));
    assertEquals(0, booksRepository.findAll().size());
  }

  @Test
  public void testDeleteBookNotExists() {
    boolean isDeleted = booksService.deleteBook(testBook.getId() + 1);

    Assertions.assertFalse(isDeleted);
  }

  @Test
  public void testDeleteBookAuthorNotWroteThisBook() {
    when(authorsRegistry.isAuthorWroteThisBook(any(), any(), any())).thenReturn(false);
    assertFalse(booksService.deleteBook(testBook.getId()));
    assertEquals(1, booksRepository.findAll().size());
  }
}
