package com.example.test.Models.DTOs;

import com.example.test.Models.Author;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Request to create a new author without any books
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class CreateAuthorRequest extends EntityDTO<Author> {
    private final String firstName;
    private final String lastName;

    @Override
    protected Author buildEntity() {
        return new Author(firstName, lastName, new ArrayList<>());
    }
}