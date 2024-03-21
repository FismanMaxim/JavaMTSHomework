package mts.homework.bookService.services.books;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.DbBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import mts.homework.bookService.services.AuthorsRegistryServiceGatewayBase;
import mts.homework.bookService.services.BooksService;
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
public class BooksServiceUpdateBookTests extends DatabaseSuite {
  @Autowired private JpaBooksRepository booksRepository;

  @Autowired private JpaAuthorsRepository authorsRepository;

  @Autowired private BooksService booksService;

  @Autowired private JpaTagsRepository tagsRepository;

  @MockBean private AuthorsRegistryServiceGatewayBase authorsRegistry;

  private Book testBook;

  @BeforeEach
  public void setUp() {
    booksRepository.deleteAll();
    authorsRepository.deleteAll();
    tagsRepository.deleteAll();

    var testAuthor = new Author("Test", "Author");
    authorsRepository.save(testAuthor);

    testBook = new Book("Test Book", testAuthor);
    booksRepository.save(testBook);
  }

  @Test
  public void updateAuthor() {
    var anotherAuthor = new Author("Another", "Author");
    authorsRepository.save(anotherAuthor);

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
    tagsRepository.save(tag);

    Optional<Book> book = booksService.addNewTag(testBook.getId(), tag.getId());

    assertTrue(book.isPresent());
    assertEquals(1, book.get().getTags().size());
  }

  @Test
  public void updateRemoveTag() {
    var tag = new Tag("Some Test Tag");
    tagsRepository.save(tag);

    testBook.assignTag(tag);
    booksRepository.save(testBook);

    Optional<Book> book = booksService.removeTag(testBook.getId(), tag.getId());

    assertTrue(book.isPresent());
    assertEquals(0, book.get().getTags().size());
  }
}
