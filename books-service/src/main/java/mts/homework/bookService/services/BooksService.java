package mts.homework.bookService.services;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.repositories.exceptions.BookNotFoundException;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import mts.homework.bookService.exceptions.InvalidBookDataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class BooksService {
//  private final BooksRepositoryBase booksRepository;
  private final JpaBooksRepository booksRepository;
  private final JpaAuthorsRepository jpaAuthorsRepository;
  private final JpaTagsRepository jpaTagsRepository;
  private final AuthorsRegistryServiceGatewayBase authorsGateway;

  private final String topic;
  private final ObjectMapper mapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public BooksService(
      JpaBooksRepository booksRepository,
      JpaAuthorsRepository jpaAuthorsRepository,
      JpaTagsRepository jpaTagsRepository,
      AuthorsRegistryServiceGatewayBase authorsGateway, @Value("${topic-to-send-get-rating-request}") String topic, ObjectMapper mapper, KafkaTemplate<String, String> kafkaTemplate) {
    this.booksRepository = booksRepository;
    this.jpaAuthorsRepository = jpaAuthorsRepository;
    this.jpaTagsRepository = jpaTagsRepository;
    this.authorsGateway = authorsGateway;
    this.topic = topic;
    this.mapper = mapper;
    this.kafkaTemplate = kafkaTemplate;
  }

  public Book createNew(BookCreationInfo creationInfo) throws InvalidBookDataException {
    if (creationInfo.authorId() == null || creationInfo.title() == null) {
      throw new InvalidBookDataException();
    }

    var targetAuthor =
        jpaAuthorsRepository
            .findById(creationInfo.authorId())
            .orElseThrow(InvalidBookDataException::new);

    if (!authorsGateway.isAuthorWroteThisBook(
        targetAuthor.getFirstName(), targetAuthor.getLastName(), creationInfo.title())) {
      throw new InvalidBookDataException();
    }

    Book book = new Book(creationInfo.title(), targetAuthor);

    return booksRepository.save(book);
  }

  public boolean deleteBook(long id) {
    var book = booksRepository.findById(id).orElseThrow();
    var author = book.getAuthor();

    if (!authorsGateway.isAuthorWroteThisBook(
        author.getFirstName(), author.getLastName(), book.getTitle())) {
      return false;
    }

    booksRepository.deleteById(id);
    return true;
  }

  record CalculateRatingRequest(long bookId) {}
  record CalculateRatingResponse(long bookId, long rating) {}

  public void calculateRating(long bookId) throws JsonProcessingException {
    String message = mapper.writeValueAsString(new CalculateRatingRequest(bookId));
    CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topic, message);
    try {
      sendResult.get(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Unexpected thread interruption", e);
    } catch (ExecutionException e) {
      throw new RuntimeException("Couldn't send message to Kafka", e);
    } catch (TimeoutException e) {
      throw new RuntimeException("Couldn't send message to Kafka due to timeout", e);
    }
  }

  @KafkaListener(topics = {"${topic-to-get-get-rating-response}"})
  @Transactional
  public void onRatingReceived(String message) throws JsonProcessingException {
    var result = mapper.readValue(message, CalculateRatingResponse.class);
    try {
      Book book = booksRepository.findById(result.bookId).orElseThrow(BookNotFoundException::new);
      book.setRating(result.rating);
      booksRepository.save(book);
    } catch (BookNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public Optional<Book> updateBookAuthor(long id, long newAuthorId) {
    var targetAuthorOpt = jpaAuthorsRepository.findById(newAuthorId);

    return targetAuthorOpt.flatMap(author -> updateBook(id, book -> book.setAuthor(author)));
  }

  public Optional<Book> findBook(long id) {
    return booksRepository.findById(id);
  }

  @Transactional
  public Optional<Book> updateBookTitle(long id, String newTitle) {
    return updateBook(id, book -> book.setTitle(newTitle));
  }

  @Transactional
  public Optional<Book> addNewTag(Long bookId, Long tagId) {
    var targetTagOpt = jpaTagsRepository.findById(tagId);

    return targetTagOpt.flatMap(tag -> updateBook(bookId, book -> book.assignTag(tag)));
  }

  @Transactional
  public Optional<Book> removeTag(Long bookId, Long tagId) {
    var targetTagOpt = jpaTagsRepository.findById(tagId);

    return targetTagOpt.flatMap(tag -> updateBook(bookId, book -> book.deassignTag(tag)));
  }

  @Transactional
  public Optional<Book> updateBook(long id, Consumer<Book> update) {
    var target = findBook(id);
    if (target.isEmpty()) return target;

    var targetBook = target.get();

    update.accept(targetBook);

    return target;
  }

  public List<Book> getBooksByTag(long tagId) {
    return booksRepository.findBooksByTag(tagId);
  }

  public List<Book> getAllBooks() {
    return booksRepository.findAll();
  }
}
