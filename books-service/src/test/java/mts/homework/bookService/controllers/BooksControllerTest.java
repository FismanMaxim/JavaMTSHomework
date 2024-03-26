package mts.homework.bookService.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.TestHelper;
import mts.homework.bookService.controllers.requests.BookCreationRequest;
import mts.homework.bookService.controllers.requests.BookUpdateRequest;
import mts.homework.bookService.controllers.responses.BookApiEntity;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import mts.homework.bookService.services.AuthorsRegistryServiceGatewayBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class BooksControllerTest extends DatabaseSuite {
  @Autowired private JpaBooksRepository booksRepository;
  @Autowired private JpaAuthorsRepository authorsRepository;
  @Autowired private JpaTagsRepository tagsRepository;
  @Autowired private TestRestTemplate http;

  @MockBean private AuthorsRegistryServiceGatewayBase authorsGateway;

  private Book testBook;
  private Author testAuthor;

  @BeforeEach
  public void setUp() {
    booksRepository.deleteAll();
    authorsRepository.deleteAll();

    testAuthor = new Author("Test", "Author");
    authorsRepository.save(testAuthor);

    testBook = new Book("Test Book", testAuthor);
    booksRepository.save(testBook);
  }

  @Test
  public void testSimpleCreation() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);
    var bookCreationRequest = new BookCreationRequest(testAuthor.getId(), testBook.getTitle());

    ResponseEntity<BookApiEntity> response =
        http.postForEntity("/api/books", bookCreationRequest, BookApiEntity.class);

    BookApiEntity result = TestHelper.assert2xxAndGetBody(response);

    assertNotNull(result);
    assertEquals(testAuthor.getId(), result.author().id());
    assertEquals(testBook.getTitle(), result.title());
  }

  @Test
  public void testCreationWithNullAuthorField() {
    var bookCreationRequest = new BookCreationRequest(null, "BookName");

    testMust4xxError(bookCreationRequest);
  }

  @Test
  public void testCreationWithNullTitleField() {
    var bookCreationRequest = new BookCreationRequest(0L, null);

    testMust4xxError(bookCreationRequest);
  }

  @Test
  public void testBookNotFound() {
    ResponseEntity<BookApiEntity> response =
        http.getForEntity(
            "/api/books/{id}", BookApiEntity.class, Map.of("id", testBook.getId() + 1));

    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  public void testDeleteBook() {
    when(authorsGateway.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);

    http.delete("/api/books/{id}", Map.of("id", testBook.getId()));

    assertEquals(0, booksRepository.findAll().size());
  }

  @Test
  public void testUpdateWithChanges() {
    BookUpdateRequest updateRequest = new BookUpdateRequest(testAuthor.getId(), "BookName");
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
    assertEquals("BookName", body.title());
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

  private void testMust4xxError(BookCreationRequest request) {
    ResponseEntity<BookApiEntity> response =
        http.postForEntity("/api/books", request, BookApiEntity.class);

    assertTrue(response.getStatusCode().is4xxClientError());
  }
}
