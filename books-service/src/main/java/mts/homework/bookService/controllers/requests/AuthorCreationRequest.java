package mts.homework.bookService.controllers.requests;

import jakarta.validation.constraints.NotNull;

public record AuthorCreationRequest(@NotNull String firstName, @NotNull String lastName) {}
