package com.example.javamtshomework.Repositories;

import com.example.javamtshomework.CustomExceptions.ItemNotFoundException;
import com.example.javamtshomework.Models.Book;
import com.example.javamtshomework.Models.DTOs.BookDTO;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryBookRepository implements BookRepository {
    private final Map<Long, Book> books;
    private final AtomicLong index;

    public long getIndex() {
        return index.getAndIncrement();
    }

    public InMemoryBookRepository() {
        books = new ConcurrentHashMap<>();
        index = new AtomicLong(0);
    }

    @Override
    public List<Book> getBooks()  {
        return new ArrayList<>(books.values());
    }

    @Override
    public Optional<Book> getBook(long id) {
        if (books.containsKey(id)) return Optional.of(books.get(id));
        else return Optional.empty();
    }

    @Override
    public List<Book> findBooksByTag(String tag) {
        return books.values().stream().filter(book -> book.hasTag(tag)).toList();
    }

    @Override
    public long createBook(BookDTO bookDTO) {
        long id = getIndex();
        Book book = bookDTO.getBook(id);
        books.put(id, book);
        return id;
    }

    @Override
    public void updateBook(Book book) {
        if (!books.containsKey(book.getId()))
            throw new ItemNotFoundException();
        books.put(book.getId(), book);
    }

    @Override
    public void deleteBook(long bookId) {
        if (!books.containsKey(bookId))
            throw new ItemNotFoundException();
        books.remove(bookId);
    }
}
