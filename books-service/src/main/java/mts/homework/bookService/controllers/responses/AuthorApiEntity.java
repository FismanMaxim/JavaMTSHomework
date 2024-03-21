package mts.homework.bookService.controllers.responses;

import mts.homework.bookService.data.entities.Author;

public record AuthorApiEntity(long id, String firstName, String lastName) {
  public static AuthorApiEntity fromAuthor(Author author) {
    return new AuthorApiEntity(author.getId(), author.getFirstName(), author.getLastName());
  }
}
