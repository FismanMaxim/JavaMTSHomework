package com.example.javamtshomework.Controllers;

import com.example.javamtshomework.Models.Book;
import com.example.javamtshomework.Models.DTOs.BookDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    @Autowired
    private TestRestTemplate rest;

    @Test
    void createBook() {
        ResponseEntity<Integer> createResponse =
                rest.postForEntity("/api/books", new BookDTO("Author", "Title", Set.of("tag1", "tag2")), Integer.class);
        assertTrue(createResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED), "Unexpected status code: " + createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        long id = createResponse.getBody();

        ResponseEntity<Book> getResponse =
                rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", id));
        assertTrue(getResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getResponse.getStatusCode());

        Book receivedBook = getResponse.getBody();
        assertNotNull(receivedBook);
        assertEquals("Author", receivedBook.getAuthor());
        assertEquals("Title", receivedBook.getTitle());
        assertTrue(receivedBook.hasTag("tag1"));
    }

    @Test
    void getBooksWithTag() {
        ResponseEntity<Integer> createResponse =
                rest.postForEntity("/api/books", new BookDTO("Author1", "Title1", Set.of("testTag")), Integer.class);
        assertTrue(createResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED), "Unexpected status code: " + createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        createResponse =
                rest.postForEntity("/api/books", new BookDTO("Author2", "Title2", Set.of("testTag")), Integer.class);
        assertTrue(createResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED), "Unexpected status code: " + createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        createResponse =
                rest.postForEntity("/api/books", new BookDTO("Author3", "Title3", Set.of("wrongTag")), Integer.class);
        assertTrue(createResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED), "Unexpected status code: " + createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        ResponseEntity<Book[]> getResponse =
                rest.getForEntity("/api/books/tagged/{tag}", Book[].class, Map.of("tag", "testTag"));
        assertTrue(getResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getResponse.getStatusCode());

        assertNotNull(getResponse.getBody());
        assertEquals(2, getResponse.getBody().length);
    }

    @Test
    void updateBook() {
        // create
        ResponseEntity<Integer> createResponse =
                rest.postForEntity("/api/books", new BookDTO("Author", "Title", Set.of("testTag")), Integer.class);
        assertTrue(createResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED), "Unexpected status code: " + createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        long id = createResponse.getBody();

        // update
        rest.put("/api/books/{id}", new BookDTO("newAuthor", "newTitle", Set.of("newTag")), Map.of("id", id));

        // get
        ResponseEntity<Book> getResponse =
                rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", id));
        assertTrue(getResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getResponse.getStatusCode());

        Book receivedBook = getResponse.getBody();
        assertNotNull(receivedBook);
        assertEquals("newAuthor", receivedBook.getAuthor());
        assertEquals("newTitle", receivedBook.getTitle());
        assertTrue(receivedBook.hasTag("newTag"));
        assertFalse(receivedBook.hasTag("testTag"));
    }

    @Test
    void deleteBook() {
        // create
        ResponseEntity<Integer> createResponse =
                rest.postForEntity("/api/books", new BookDTO("Author", "Title", Set.of("testTag")), Integer.class);
        assertTrue(createResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED), "Unexpected status code: " + createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        long id = createResponse.getBody();

        // delete
        rest.delete("/api/books/{id}", Map.of("id", id));

        // get
        ResponseEntity<Book> getResponse =
                rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", id));
        assertTrue(getResponse.getStatusCode().is4xxClientError(), "Unexpected status code: " + getResponse.getStatusCode());

        // delete again
        rest.delete("/api/books/{id}", Map.of("id", id));

    }
}