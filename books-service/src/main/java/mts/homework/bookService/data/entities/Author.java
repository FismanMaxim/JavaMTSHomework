package mts.homework.bookService.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Author {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  @NotNull
  @Column(name = "first_name", nullable = false, length = Integer.MAX_VALUE)
  private String firstName;

  @NotNull
  @Column(name = "last_name", nullable = false, length = Integer.MAX_VALUE)
  private String lastName;

  public Author(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }
}
