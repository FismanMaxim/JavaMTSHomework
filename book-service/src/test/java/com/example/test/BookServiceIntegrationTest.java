package com.example.test;

import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.Tag;
import com.example.test.Services.AuthorService;
import com.example.test.Services.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookServiceIntegrationTest {
    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Test
    void testIntegrationWithRepositoryOnBookLifecycle() {
        // create book components
        Author author = new Author("name", "surname");
        Tag tag1 = new Tag("tag1");
        Book book = new Book(author, "title", Set.of(tag1));

        // create book
        Book savedBook = bookService.createBook(book);
        assertEquals(book.getTitle(), savedBook.getTitle());
        long bookId = savedBook.getId();
        long authorId = savedBook.getAuthor().getId();

        // check author
        Optional<Author> savedAuthorOpt = authorService.findById(authorId);
        Author savedAuthor = null;
        if (savedAuthorOpt.isPresent()) savedAuthor = savedAuthorOpt.get();
        else fail();
        assertNotNull(savedAuthor);
        assertEquals(author.getFirstName(), savedAuthor.getFirstName());
        assertEquals(author.getLastName(), savedAuthor.getLastName());

        // change book author
        Author newAuthor = new Author("newName", "newSurname");
        long newAuthorId = authorService.createAuthor(newAuthor).getId();
        bookService.changeBookAuthor(bookId, newAuthorId);

        // Check new author
        Optional<Book> retrievedBookOpt = bookService.getBook(bookId);
        if (retrievedBookOpt.isEmpty()) fail();
        else assertEquals(newAuthorId, retrievedBookOpt.get().getAuthor().getId());

        // delete book
        bookService.deleteBook(bookId);
        retrievedBookOpt = bookService.getBook(bookId);
        assertTrue(retrievedBookOpt.isEmpty());
    }
}
