package mts.homework.bookService.services;

import java.util.Optional;

public record AuthorUpdateDto(Optional<String> newFirstName, Optional<String> newLastName) {}
