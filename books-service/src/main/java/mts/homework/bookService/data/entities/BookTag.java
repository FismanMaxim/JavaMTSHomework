package mts.homework.bookService.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "books_tags")
@Getter
@Setter
public class BookTag {
  @EmbeddedId private BookTagId id;

  @MapsId("bookId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;
}
