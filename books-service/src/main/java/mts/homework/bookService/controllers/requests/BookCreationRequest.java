package mts.homework.bookService.controllers.requests;

import jakarta.validation.constraints.NotNull;

public record BookCreationRequest(@NotNull Long authorId, @NotNull String title) {
  public BookCreationRequest(Long authorId, String title) {
    this.title = title;
    this.authorId = authorId;
  }

  @Override
  public Long authorId() {
    return authorId;
  }

  @Override
  public String title() {
    return title;
  }
}
