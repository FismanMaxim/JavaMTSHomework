package com.example.javamtshomework.Controllers;

import com.example.javamtshomework.CustomExceptions.ApiError;
import com.example.javamtshomework.CustomExceptions.ItemNotFoundException;
import com.example.javamtshomework.CustomExceptions.UpdateModelException;
import com.example.javamtshomework.Models.Book;
import com.example.javamtshomework.Models.DTOs.BookDTO;
import com.example.javamtshomework.Services.BookService;
import jakarta.validation.Valid;
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
        return bookService.getAllBooks();
    }

    @GetMapping("{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") long id) {
        var bookOpt = bookService.getBook(id);
        if (bookOpt.isPresent()) return new ResponseEntity<>(bookOpt.get(), HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long createBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.createBook(bookDTO);
    }

    @PutMapping("{id}")
    public void updateBook(@PathVariable long id,
                           @Valid @RequestBody BookDTO bookDTO) {
        Book book = bookService.getBook(id).orElseThrow(UpdateModelException::new);
        if (!bookDTO.author().isBlank())
            book.setAuthor(bookDTO.author());
        if (!bookDTO.title().isBlank())
            book.setTitle(bookDTO.title());
        if (bookDTO.tags() != null)
            book.setTags(bookDTO.tags());
        bookService.updateBook(book);
    }

    @DeleteMapping("{id}")
    public void deleteBook(@PathVariable long id) {
        try {
            bookService.deleteBook(id);
        } catch (ItemNotFoundException e) {
            throw new RestClientException("Cannot delete book because it does not exist");
        }
    }

    @GetMapping("tagged/{tag}")
    public List<Book> findBooksWithTag(@PathVariable String tag) {
        return bookService.findBooksByTag(tag);
    }


    @ExceptionHandler
    public ResponseEntity<ApiError> itemNotFoundExceptionHandler(RestClientException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
