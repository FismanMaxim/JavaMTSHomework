package com.example.test.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull(message = "Author cannot be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private Author author;

    @NotNull(message = "Title is not defined")
    private String title;

    @NotNull(message = "Tags are not defined")
    @Getter(AccessLevel.NONE)
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "book_tag",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    public Book(Author author, String title, Set<Tag> tags) {
        this.author = author;
        this.title = title;
        this.tags = tags;
    }

    public boolean hasTag(String tagName) {
        return tags.stream().anyMatch(tag -> tag.getName().equals(tagName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id != null && id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
