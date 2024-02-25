package com.example.test.Models.DTOs;

import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


public record CreateAuthorRequest(String firstName, String lastName) {
    @JsonIgnore
    public Author getAuthor() {
        return new Author(firstName, lastName);
    }
}