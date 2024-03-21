package mts.homework.bookService.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Embeddable
@Getter
@Setter
public class BookTagId implements Serializable {
  private static final long serialVersionUID = 4509413700289921245L;

  @NotNull
  @Column(name = "book_id", nullable = false)
  private Long bookId;

  @NotNull
  @Column(name = "tag_id", nullable = false)
  private Long tagId;


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BookTagId bookTagId = (BookTagId) o;
    return Objects.equals(bookId, bookTagId.bookId) && Objects.equals(tagId, bookTagId.tagId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagId, bookId);
  }
}
