package com.example.javamtshomework.Models.DTOs;

import com.example.javamtshomework.Models.Book;

import java.util.Set;

public record BookDTO(String author, String title, Set<String> tags) {
    public Book getBook(long id) {
        return new Book(id, author, title, tags);
    }
}
