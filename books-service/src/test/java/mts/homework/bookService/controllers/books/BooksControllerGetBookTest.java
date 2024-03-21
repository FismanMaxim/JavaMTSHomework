package mts.homework.bookService.controllers.books;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.TestHelper;
import mts.homework.bookService.controllers.responses.BookApiEntity;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksControllerGetBookTest extends DatabaseSuite {
  @Autowired private JpaBooksRepository booksRepository;

  @Autowired private JpaAuthorsRepository authorsRepository;

  @Autowired private TestRestTemplate http;

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
  public void testSimpleGetBook() {
    ResponseEntity<BookApiEntity> response =
        http.getForEntity("/api/books/{id}", BookApiEntity.class, Map.of("id", testBook.getId()));

    BookApiEntity body = TestHelper.assert2xxAndGetBody(response);

    assertNotNull(body);
    assertEquals(testBook.getTitle(), body.title());
  }

  @Test
  public void testBookNotFound() {
    ResponseEntity<BookApiEntity> response =
        http.getForEntity(
            "/api/books/{id}", BookApiEntity.class, Map.of("id", testBook.getId() + 1));

    assertTrue(response.getStatusCode().is4xxClientError());
  }
}
