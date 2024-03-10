package com.example.test.Controllers;

import com.example.test.CustomExceptions.ApiError;
import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.Book;
import com.example.test.Models.DTOs.BookDTO;
import com.example.test.Services.BookService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookService bookService;

  private final RestTemplate restTemplate;

  @Autowired
  public BookController(BookService bookService, RestTemplate restTemplate) {
    this.bookService = bookService;
    this.restTemplate = restTemplate;
  }

  @GetMapping()
  public List<Book> getAllBooks() {
    return bookService.getAll();
  }

  @GetMapping("{id}")
  public ResponseEntity<Book> getBook(@PathVariable("id") long id) {
    var bookOpt = bookService.findById(id);
    return bookOpt
        .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
  }

  /**
   * Creates a new book with given values (changes only defined values)
   *
   * @param bookDTO boot with new values
   */
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  @RateLimiter(name = "createBook", fallbackMethod = "fallbackCreateBook")
  @CircuitBreaker(name = "createBook", fallbackMethod = "fallbackCircuitBreaker")
  @Retry(name = "createBook")
  public Book createBook(@Valid @RequestBody BookDTO bookDTO, String requestId) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.add("X-REQUEST_ID", requestId);

      restTemplate.exchange(
          "/api/author-registry",
          HttpMethod.POST,
          new HttpEntity<>(
              Map.of(
                  "firstName",
                  bookDTO.getAuthor().getFirstName(),
                  "secondName",
                  bookDTO.getAuthor().getLastName(),
                  "bookName",
                  bookDTO.getTitle()),
              headers),
          void.class);

      return bookService.create(bookDTO.get());
    } catch (RestClientException e) {
      throw new UnsupportedOperationException();
    }
  }

  public Book fallbackCreateBook(BookDTO bookDTO, String requestId, RequestNotPermitted requestNotPermitted) {
    throw new RestClientException(requestNotPermitted.getMessage(), requestNotPermitted);
  }

  public Book fallbackCircuitBreaker(BookDTO bookDTO, String requestId, RequestNotPermitted requestNotPermitted) {
    throw new RestClientException(requestNotPermitted.getMessage(), requestNotPermitted);
  }

  @PutMapping("{id}/title")
  public void updateBookTitle(@PathVariable long id, @NotNull String newTitle) {
    bookService.updateBookTitle(id, newTitle);
  }

  @DeleteMapping("{id}")
  public void deleteBook(@PathVariable long id) {
    Optional<Book> bookOpt = bookService.findById(id);
    if (bookOpt.isEmpty())
      throw new RestClientException("Cannot delete book because it does not exist");
    Book book = bookOpt.get();

    ResponseEntity<Boolean> hasAuthorBook =
        restTemplate.getForEntity(
            "/api/author-registry/is-author",
            boolean.class,
            (Object)
                Map.of(
                    "firstName",
                    book.getAuthor().getFirstName(),
                    "secondName",
                    book.getAuthor().getLastName(),
                    "bookName",
                    book.getTitle()));
    if (hasAuthorBook.getBody() == null)
      throw new RestClientException("Failed to load info about the author");
    if (!hasAuthorBook.getBody())
      throw new RestClientException("Cannot remove book because it does not belong to this author");

    try {
      bookService.delete(id);
    } catch (ItemNotFoundException e) {
      throw new RestClientException("Cannot delete book because it does not exist");
    }
  }

  @GetMapping("tagged/{tagId}")
  public List<Book> findBooksWithTag(@PathVariable Long tagId) {
    return bookService.findBooksByTag(tagId);
  }

  @PutMapping("{bookId}/changeAuthor/{newAuthorId}")
  public void changeAuthor(@PathVariable long bookId, @PathVariable long newAuthorId) {
    try {
      bookService.changeBookAuthor(bookId, newAuthorId);
    } catch (ItemNotFoundException e) {
      throw new RestClientException("Failed to change author of the book");
    }
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> itemNotFoundExceptionHandler(RestClientException e) {
    return new ResponseEntity<>(new ApiError(e.getMessage()), HttpStatus.NOT_FOUND);
  }
}
