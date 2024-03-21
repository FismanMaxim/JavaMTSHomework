package mts.homework.bookService.controllers;

import java.util.Optional;
import mts.homework.bookService.controllers.requests.AuthorCreationRequest;
import mts.homework.bookService.controllers.requests.AuthorUpdateRequest;
import mts.homework.bookService.controllers.responses.AuthorApiEntity;
import mts.homework.bookService.services.AuthorUpdateDto;
import mts.homework.bookService.services.AuthorsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/authors")
public class AuthorsController {
  private final AuthorsService authorsService;

  public AuthorsController(AuthorsService authorsService) {
    this.authorsService = authorsService;
  }

  @GetMapping("{id}")
  public ResponseEntity<AuthorApiEntity> findAuthor(@PathVariable("id") long id) {
    var target = authorsService.findAuthor(id);

    return target
        .map(author -> ResponseEntity.ok(AuthorApiEntity.fromAuthor(author)))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PostMapping
  public ResponseEntity<AuthorApiEntity> createAuthor(
      @RequestBody AuthorCreationRequest creationInfo) {
    return ResponseEntity.ok(
        AuthorApiEntity.fromAuthor(
            authorsService.createNew(creationInfo.firstName(), creationInfo.lastName())));
  }

  @PatchMapping("{id}")
  public ResponseEntity<AuthorApiEntity> updateAuthor(
      @PathVariable("id") long id, @RequestBody AuthorUpdateRequest updateRequest) {
    var updateDto =
        new AuthorUpdateDto(
            Optional.ofNullable(updateRequest.newFirstName()),
            Optional.ofNullable(updateRequest.newLastName()));

    var result = authorsService.updateAuthor(id, updateDto);

    return result
        .map(author -> ResponseEntity.ok(AuthorApiEntity.fromAuthor(author)))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @DeleteMapping("{id}")
  public void deleteAuthor(@PathVariable long id) {
    authorsService.deleteAuthor(id);
  }
}
