package com.example.test.Controllers;

import com.example.test.CustomExceptions.ApiError;
import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.Author;
import com.example.test.Models.DTOs.CreateAuthorRequest;
import com.example.test.Services.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable long id) {
        var authorOpt = authorService.findById(id);
        if (authorOpt.isPresent()) return new ResponseEntity<>(authorOpt.get(), HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Author createAuthor(@Valid @RequestBody CreateAuthorRequest createAuthorRequest) {
        return authorService.createAuthor(createAuthorRequest.getAuthor());
    }

    @PutMapping("{id}")
    public void updateAuthor(@PathVariable long id,
                           @Valid @RequestBody CreateAuthorRequest createAuthorRequest) {
        authorService.updateAuthor(id, createAuthorRequest);
    }

    @DeleteMapping("{id}")
    public void deleteBook(@PathVariable long id) {
        try {
            authorService.deleteAuthorById(id);
        } catch (ItemNotFoundException e) {
            throw new RestClientException("Cannot delete author by id because it does not exist: id=" + id);
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
