package mts.homework.bookService.data.repositories;

import java.util.List;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.repositories.exceptions.BookNotFoundException;

public interface BooksRepositoryBase {
  Book createBook(Author author, String title);

  Book findBook(long id) throws BookNotFoundException;

  void deleteBook(long id) throws BookNotFoundException;

  List<Book> getByTag(long tagId);

  List<Book> getAllBooks();
}
