package com.example.test.Models.DTOs;

import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;

@Getter
public final class BookDTO {
    private final Author author;
    private final String title;
    private final Set<Tag> tags;
    private Book book;

    public BookDTO(Author author, String title, Set<Tag> tags) {
        this.author = author;
        this.title = title;
        this.tags = tags;
    }

    @JsonIgnore
    public Book getBook() {
        if (book == null) book = new Book(author, title, tags);
        return book;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BookDTO) obj;
        return Objects.equals(this.author, that.author) &&
                Objects.equals(this.title, that.title) &&
                Objects.equals(this.tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, tags);
    }
}
