package mts.homework.bookService.controllers.books;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.TestHelper;
import mts.homework.bookService.controllers.requests.BookUpdateRequest;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksControllerUpdateBookTest extends DatabaseSuite {
  @Autowired private JpaBooksRepository booksRepository;

  @Autowired private JpaAuthorsRepository authorsRepository;

  @Autowired private TestRestTemplate http;

  @Autowired private JpaTagsRepository tagsRepository;

  private Book testBook;
  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    booksRepository.deleteAll();
    authorsRepository.deleteAll();
    tagsRepository.deleteAll();

    testAuthor = new Author("Test", "Author");
    authorsRepository.save(testAuthor);

    testBook = new Book("Test Book", testAuthor);
    booksRepository.save(testBook);
    http.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @Test
  public void testUpdateWithChanges() {
    BookUpdateRequest updateRequest = new BookUpdateRequest(testAuthor.getId(), "CLR via C#");
    HttpEntity<BookUpdateRequest> updateRequestEntity = new HttpEntity<>(updateRequest);
    ResponseEntity<BookApiEntity> response =
        http.exchange(
            "/api/books/{id}",
            HttpMethod.PATCH,
            updateRequestEntity,
            BookApiEntity.class,
            Map.of("id", testBook.getId()));

    BookApiEntity body = TestHelper.assert2xxAndGetBody(response);

    assertNotNull(body);
    assertEquals(testAuthor.getId(), body.author().id());
    assertEquals("CLR via C#", body.title());
  }

  @Test
  public void testUpdateNotFound() {
    BookUpdateRequest updateRequest = new BookUpdateRequest(1L, "CLR via C#");
    HttpEntity<BookUpdateRequest> updateRequestEntity = new HttpEntity<>(updateRequest);
    ResponseEntity<BookApiEntity> response =
        http.exchange(
            "/api/books/{id}",
            HttpMethod.PATCH,
            updateRequestEntity,
            BookApiEntity.class,
            Map.of("id", testBook.getId() + 1));

    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  public void testNoUpdate() {
    BookUpdateRequest updateRequest = new BookUpdateRequest(null, null);

    HttpEntity<BookUpdateRequest> updateRequestEntity = new HttpEntity<>(updateRequest);
    ResponseEntity<BookApiEntity> response =
        http.exchange(
            "/api/books/{id}",
            HttpMethod.PATCH,
            updateRequestEntity,
            BookApiEntity.class,
            Map.of("id", testBook.getId()));

    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  public void testUpdateNewTag() {
    Tag testTag = new Tag("Test Tag");
    tagsRepository.save(testTag);

    ResponseEntity<BookApiEntity> response =
        http.exchange(
            "/api/books/{book_id}/tags/{tag_id}",
            HttpMethod.PATCH,
            null,
            BookApiEntity.class,
            Map.of("book_id", testBook.getId(), "tag_id", testTag.getId()));

    BookApiEntity body = TestHelper.assert2xxAndGetBody(response);

    assertNotNull(body);
    assertEquals(testAuthor.getId(), body.author().id());
    assertEquals(1, body.tags().size());
    assertEquals(testTag.getId(), body.tags().get(0).id());
  }

  @Test
  public void testUpdateNewTagTargetEntitiesNotFound() {
    Tag testTag = new Tag("Test Tag");
    tagsRepository.save(testTag);

    ResponseEntity<BookApiEntity> response =
        http.exchange(
            "/api/books/{book_id}/tags/{tag_id}",
            HttpMethod.PATCH,
            null,
            BookApiEntity.class,
            Map.of("book_id", testBook.getId(), "tag_id", testTag.getId() + 1));

    assertTrue(response.getStatusCode().is4xxClientError());

    response =
        http.exchange(
            "/api/books/{book_id}/tags/{tag_id}",
            HttpMethod.PATCH,
            null,
            BookApiEntity.class,
            Map.of("book_id", testBook.getId() + 1, "tag_id", testTag.getId()));

    assertTrue(response.getStatusCode().is4xxClientError());

    response =
        http.exchange(
            "/api/books/{book_id}/tags/{tag_id}",
            HttpMethod.PATCH,
            null,
            BookApiEntity.class,
            Map.of("book_id", testBook.getId() + 1, "tag_id", testTag.getId() + 1));

    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  public void testUpdateRemoveTag() {
    Tag testTag = new Tag("Test Tag");
    tagsRepository.save(testTag);

    testBook.assignTag(testTag);
    booksRepository.save(testBook);

    ResponseEntity<BookApiEntity> response =
        http.exchange(
            "/api/books/{book_id}/tags/{tag_id}",
            HttpMethod.DELETE,
            null,
            BookApiEntity.class,
            Map.of("book_id", testBook.getId(), "tag_id", testTag.getId()));

    BookApiEntity body = TestHelper.assert2xxAndGetBody(response);

    assertNotNull(body);
    assertEquals(testAuthor.getId(), body.author().id());
    assertEquals(0, body.tags().size());
  }

  @Test
  public void testUpdateRemoveTagTargetEntitiesNotFound() {
    Tag testTag = new Tag("Test Tag");
    tagsRepository.save(testTag);

    ResponseEntity<BookApiEntity> response =
        http.exchange(
            "/api/books/{book_id}/tags/{tag_id}",
            HttpMethod.DELETE,
            null,
            BookApiEntity.class,
            Map.of("book_id", testBook.getId(), "tag_id", testTag.getId() + 1));

    assertTrue(response.getStatusCode().is4xxClientError());

    response =
        http.exchange(
            "/api/books/{book_id}/tags/{tag_id}",
            HttpMethod.DELETE,
            null,
            BookApiEntity.class,
            Map.of("book_id", testBook.getId() + 1, "tag_id", testTag.getId()));

    assertTrue(response.getStatusCode().is4xxClientError());

    response =
        http.exchange(
            "/api/books/{book_id}/tags/{tag_id}",
            HttpMethod.DELETE,
            null,
            BookApiEntity.class,
            Map.of("book_id", testBook.getId() + 1, "tag_id", testTag.getId() + 1));

    assertTrue(response.getStatusCode().is4xxClientError());
  }
}
