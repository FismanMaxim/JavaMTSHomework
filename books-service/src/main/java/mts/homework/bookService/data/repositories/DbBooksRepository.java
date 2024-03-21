package mts.homework.bookService.data.repositories;

import java.util.List;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.entities.Book;
import mts.homework.bookService.data.repositories.exceptions.BookNotFoundException;
import mts.homework.bookService.data.repositories.jpa.JpaBooksRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DbBooksRepository implements BooksRepositoryBase {
  private final JpaBooksRepository jpaBooksRepository;

  public DbBooksRepository(JpaBooksRepository jpaBooksRepository) {

    this.jpaBooksRepository = jpaBooksRepository;
  }

  @Override
  @Transactional
  public Book createBook(Author author, String title) {
    return jpaBooksRepository.save(new Book(title, author));
  }

  @Override
  @Transactional
  public Book findBook(long id) throws BookNotFoundException {
    var target = jpaBooksRepository.findById(id);

    if (target.isEmpty()) {
      throw new BookNotFoundException();
    }

    return target.get();
  }

  @Override
  @Transactional
  public void deleteBook(long id) throws BookNotFoundException {
    var targetBook = findBook(id);

    jpaBooksRepository.delete(targetBook);
  }

  @Override
  @Transactional
  public List<Book> getByTag(long tagId) {
    return jpaBooksRepository.findBooksByTag(tagId);
  }

  @Override
  @Transactional
  public List<Book> getAllBooks() {
    return jpaBooksRepository.findAll();
  }
}
