package mts.homework.bookService.data.repositories.jpa;

import java.util.List;
import mts.homework.bookService.data.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaBooksRepository extends JpaRepository<Book, Long> {
  @Query("""
    SELECT bt.book FROM BookTag bt
    WHERE bt.id.tagId = :tagId
  """)
  List<Book> findBooksByTag(Long tagId);
}
