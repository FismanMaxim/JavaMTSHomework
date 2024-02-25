package com.example.test.Models.DTOs;

import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.Tag;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;

@Getter
public final class BookDTO extends EntityDTO<Book> {
    private final Author author;
    private final String title;
    private final Set<Tag> tags;

    public BookDTO(Author author, String title, Set<Tag> tags) {
        this.author = author;
        this.title = title;
        this.tags = tags;
    }

    @Override
    protected Book buildEntity() {
        return new Book(author, title, tags);
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
