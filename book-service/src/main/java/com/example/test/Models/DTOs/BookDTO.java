package com.example.test.Models.DTOs;

import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.Tag;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public final class BookDTO extends EntityDTO<Book> {
    private final Author author;
    private final String title;
    private final Set<Tag> tags;

    @Override
    protected Book buildEntity() {
        return new Book(author, title, tags);
    }
}
