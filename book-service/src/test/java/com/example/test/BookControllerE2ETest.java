package com.example.test;

import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.DTOs.BookDTO;
import com.example.test.Models.DTOs.CreateAuthorRequest;
import com.example.test.Models.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerE2ETest {
    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

    @Autowired
    private TestRestTemplate rest;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void bookLifecycleTest() {
        // Create author
        Author author = new Author("John", "Doe");

        // Create book
        final String bookTitle = "Title";
        ResponseEntity<Book> createBookResponse =
                rest.postForEntity("/api/books", new BookDTO(author, bookTitle, Set.of(new Tag("tag1"))), Book.class);
        assertEquals(HttpStatus.CREATED,  createBookResponse.getStatusCode());
        long bookId = Objects.requireNonNull(createBookResponse.getBody()).getId();

        // Retrieve book
        ResponseEntity<Book> getBookResponse =
                rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", bookId));
        assertEquals(HttpStatus.OK,  getBookResponse.getStatusCode());
        Book retrievedBook = getBookResponse.getBody();
        assertNotNull(retrievedBook);
        assertEquals(bookId, retrievedBook.getId());
        assertEquals(author.getFirstName(), retrievedBook.getAuthor().getFirstName());
        assertEquals(bookTitle, retrievedBook.getTitle());

        // Create new author
        ResponseEntity<Author> createAuthorResponse
                =rest.postForEntity("/api/authors", new CreateAuthorRequest("newName", "newSurname"), Author.class);
        assertEquals(HttpStatus.CREATED, createAuthorResponse.getStatusCode());
        long newAuthorId = Objects.requireNonNull(createAuthorResponse.getBody()).getId();

        // Change author name
        rest.put("/api/books/{bookId}/changeAuthor/{authorId}", null, Map.of("bookId", bookId, "authorId", newAuthorId));

        // Retrieve book and check new author
        getBookResponse = rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", bookId));
        assertEquals(HttpStatus.OK,  getBookResponse.getStatusCode());
        retrievedBook = getBookResponse.getBody();
        assertEquals("newName", Objects.requireNonNull(retrievedBook).getAuthor().getFirstName());
    }
}
