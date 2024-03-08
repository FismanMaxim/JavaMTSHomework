package com.mipt.hsse.tech.rentservice.Domain.Entities;

import com.mipt.hsse.tech.rentservice.Domain.PersonName;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Person {
  @Getter(AccessLevel.NONE)
  @Id
  private long Id;

  @Embedded
  private PersonName name;

  @Email
  private String email;


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person person = (Person) o;
    return Id == person.Id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(Id);
  }
}
