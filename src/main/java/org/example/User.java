package org.example;

public class User {
  private String firstName;
  private String secondName;

  public User(String firstName, String secondName) {
    this.firstName = firstName;
    this.secondName = secondName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getSecondName() {
    return secondName;
  }
}
