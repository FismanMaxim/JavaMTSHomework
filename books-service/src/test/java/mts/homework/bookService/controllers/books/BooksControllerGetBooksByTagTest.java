package mts.homework.bookService.controllers.books;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.TestHelper;
import mts.homework.bookService.controllers.responses.BookApiEntity;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksControllerGetBooksByTagTest extends DatabaseSuite {
  @Autowired private JpaBooksRepository booksRepository;

  @Autowired private JpaAuthorsRepository authorsRepository;

  @Autowired private TestRestTemplate http;

  @Autowired private JpaTagsRepository tagsRepository;

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
  public void testGetByTags() {
    var testTag = new Tag("Test tag");
    tagsRepository.save(testTag);

    testBook.assignTag(testTag);
    booksRepository.save(testBook);

    ResponseEntity<BookApiEntity[]> booksResult =
        http.getForEntity(
            "/api/books/tags/{tag}", BookApiEntity[].class, Map.of("tag", testTag.getId()));

    BookApiEntity[] body = TestHelper.assert2xxAndGetBody(booksResult);

    assertEquals(1, body.length);
    assertEquals(testBook.getId(), body[0].id());
  }

  @Test
  public void testGetByTagsEmpty() {
    var testTag = new Tag("Test tag");
    tagsRepository.save(testTag);

    testBook.assignTag(testTag);
    booksRepository.save(testBook);

    ResponseEntity<BookApiEntity[]> booksResult =
        http.getForEntity(
            "/api/books/tags/{tag}", BookApiEntity[].class, Map.of("tag", testTag.getId() + 1));

    BookApiEntity[] body = TestHelper.assert2xxAndGetBody(booksResult);

    assertNotNull(body);
    assertEquals(0, body.length);
  }
}
