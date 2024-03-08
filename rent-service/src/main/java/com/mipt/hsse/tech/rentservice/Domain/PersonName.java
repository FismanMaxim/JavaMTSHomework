package com.mipt.hsse.tech.rentservice.Domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PersonName {
  public String firstName;
  public String secondName;
}
