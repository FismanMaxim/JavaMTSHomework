package com.example.test.Controllers;

import com.example.test.CustomExceptions.ApiError;
import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.Book;
import com.example.test.Models.DTOs.BookDTO;
import com.example.test.Services.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping()
    public List<Book> getAllBooks() {
        return bookService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") long id) {
        var bookOpt = bookService.findById(id);
        return bookOpt.map(book -> new ResponseEntity<>(book, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a new book with given values (changes only defined values)
     *
     * @param bookDTO boot with new values
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.create(bookDTO.get());
    }

    @PutMapping("{id}/title")
    public void updateBookTitle(@PathVariable long id, @NotNull String newTitle) {
        bookService.updateBookTitle(id, newTitle);
    }

    @DeleteMapping("{id}")
    public void deleteBook(@PathVariable long id) {
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
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
