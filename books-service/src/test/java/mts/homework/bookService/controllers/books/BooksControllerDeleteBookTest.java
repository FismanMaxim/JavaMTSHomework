package mts.homework.bookService.controllers.books;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import mts.homework.bookService.services.AuthorsRegistryServiceGatewayBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class BooksControllerDeleteBookTest extends DatabaseSuite {
  @Autowired private JpaBooksRepository booksRepository;

  @Autowired private JpaAuthorsRepository authorsRepository;

  @Autowired private TestRestTemplate http;

  @MockBean private AuthorsRegistryServiceGatewayBase authorGatewayBase;

  private Book testBook;

  @BeforeEach
  public void setUp() {
    booksRepository.deleteAll();
    authorsRepository.deleteAll();

    Author testAuthor = new Author("Test", "Author");
    authorsRepository.save(testAuthor);

    testBook = new Book("Test Book", testAuthor);
    booksRepository.save(testBook);
  }

  @Test
  public void testDeleteBook() {
    when(authorGatewayBase.isAuthorWroteThisBook(any(), any(), any())).thenReturn(true);

    http.delete("/api/books/{id}", Map.of("id", testBook.getId()));

    assertEquals(0, booksRepository.findAll().size());
  }
}
