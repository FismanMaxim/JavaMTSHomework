package com.example.javamtshomework.Services;

import com.example.javamtshomework.Models.Book;
import com.example.javamtshomework.Models.DTOs.BookDTO;
import com.example.javamtshomework.Repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.getBooks();
    }

    public Optional<Book> getBook(long bookId) {
        return bookRepository.getBook(bookId);
    }

    public List<Book> findBooksByTag(String tag) {
        return bookRepository.findBooksByTag(tag);
    }

    public long createBook(BookDTO bookDTO) {
        return bookRepository.createBook(bookDTO);
    }

    public void updateBook(Book book) {
        bookRepository.updateBook(book);
    }

    public void deleteBook(long bookIndex) {
        bookRepository.deleteBook(bookIndex);
    }
}
