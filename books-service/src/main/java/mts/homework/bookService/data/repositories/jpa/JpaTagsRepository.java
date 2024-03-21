package mts.homework.bookService.data.repositories.jpa;

import java.util.Optional;
import mts.homework.bookService.data.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTagsRepository extends JpaRepository<Tag, Long> {
  Optional<Tag> findByName(String name);
}
