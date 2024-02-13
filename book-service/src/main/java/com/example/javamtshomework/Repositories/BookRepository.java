package com.example.javamtshomework.Repositories;

import com.example.javamtshomework.Models.Book;
import com.example.javamtshomework.Models.DTOs.BookDTO;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> getBooks();

    Optional<Book> getBook(long id);

    List<Book> findBooksByTag(String tag);

    long createBook(BookDTO bookDTO);

    void updateBook(Book book);

    void deleteBook(long bookId);
}
