package com.example.test.Models.DTOs;

import com.example.test.Models.Author;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Request to create a new author without any books
 */
@Getter
public final class CreateAuthorRequest extends EntityDTO<Author> {
    private final String firstName;
    private final String lastName;

    public CreateAuthorRequest(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    protected Author buildEntity() {
        return new Author(firstName, lastName, new ArrayList<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CreateAuthorRequest) obj;
        return Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}