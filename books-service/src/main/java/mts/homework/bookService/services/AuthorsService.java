package mts.homework.bookService.services;

import java.util.Optional;
import mts.homework.bookService.data.entities.Author;
import mts.homework.bookService.data.repositories.jpa.JpaAuthorsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorsService {
  private final JpaAuthorsRepository authorsRepository;

  public AuthorsService(JpaAuthorsRepository authorsRepository) {

    this.authorsRepository = authorsRepository;
  }

  @Transactional
  public Optional<Author> findAuthor(long id) {
    return authorsRepository.findById(id);
  }

  @Transactional
  public Optional<Author> updateAuthor(Long id, AuthorUpdateDto update) {
    var targetAuthorOpt = authorsRepository.findById(id);
    if (targetAuthorOpt.isEmpty()) return targetAuthorOpt;

    var targetAuthor = targetAuthorOpt.get();
    if (update.newFirstName().isPresent()) {
      targetAuthor.setFirstName(update.newFirstName().get());
    }

    if (update.newLastName().isPresent()) {
      targetAuthor.setLastName(update.newLastName().get());
    }

    return targetAuthorOpt;
  }

  @Transactional
  public Author createNew(String firstName, String lastName) {
    var author = new Author(firstName, lastName);

    return authorsRepository.save(author);
  }

  @Transactional
  public boolean deleteAuthor(Long id) {
    var target = authorsRepository.findById(id);

    if (target.isEmpty()) return false;

    authorsRepository.deleteById(id);

    return true;
  }
}
