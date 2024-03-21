package mts.homework.bookService.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  @NotNull
  @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
  private String name;

  public Tag(String name) {
    this.name = name;
  }
}
