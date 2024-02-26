package com.example.test.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull(message = "Author name cannot be empty")
    private String firstName;

    @NotNull(message = "Author name cannot be empty")
    private String lastName;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Book> books;

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        books = new ArrayList<>();
    }

    public Author(String firstName, String lastName, List<Book> books) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.books = books;
    }

    @JsonIgnore
    public List<Book> getBooksCopy() {
        return new ArrayList<>(books);
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id != null && id.equals(author.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
