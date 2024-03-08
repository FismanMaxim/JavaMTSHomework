package com.mipt.hsse.tech.rentservice.Domain.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Item {
  @Setter(AccessLevel.NONE)
  @Id
  private long id;

  private long typeId;

  private String displayName;
}
