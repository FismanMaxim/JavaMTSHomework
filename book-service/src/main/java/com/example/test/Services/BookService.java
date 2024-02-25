package com.example.test.Services;

import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.Tag;
import com.example.test.Repositories.AuthorRepository;
import com.example.test.Repositories.BookRepository;
import com.example.test.Repositories.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService implements CrudService<Book, Long> {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, TagRepository tagRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> findById(Long bookId) {
        return bookRepository.findById(bookId);
    }

    @Transactional
    @Override
    public Book create(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> findBooksByTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(
                () -> new ItemNotFoundException("Tag with given id not found: id=" + tagId));
        return bookRepository.findBookByTagsContains(tag);
    }

    @Transactional
    public void updateBookTitle(Long bookId, String newTitle) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new ItemNotFoundException("Cannot update book with given id because it does not exist: id=" + bookId));
        book.setTitle(newTitle);
        bookRepository.save(book);
    }

    @Transactional
    @Override
    public void delete(Long bookIndex) {
        bookRepository.deleteById(bookIndex);
    }

    @Transactional
    public void changeBookAuthor(long bookId, long authorId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new ItemNotFoundException("Cannot find book with id=" + bookId));
        Author author = authorRepository.findById(authorId).orElseThrow(
                () -> new ItemNotFoundException("Cannot find author with id=" + authorId));

        Optional<Author> previousAuthorOpt = authorRepository.findById(book.getAuthor().getId());

        // Remove this book from the previous author
        if (previousAuthorOpt.isPresent()) {
            Author previousAuthor = previousAuthorOpt.get();
            previousAuthor.removeBook(book);
            authorRepository.save(previousAuthor);
        }

        book.setAuthor(author);
        author.addBook(book);

        authorRepository.save(author);
        bookRepository.save(book);
    }
}
