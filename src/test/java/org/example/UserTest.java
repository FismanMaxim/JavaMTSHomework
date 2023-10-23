package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

  @Test
  void getFirstName() {
    // Assign
    String firstName = "testFirstName";
    String secondName = "testSecondName";
    User user = new User(firstName, secondName);

    // Act
    String actualFirstName = user.getFirstName();

    // Assert
    assertEquals(firstName, actualFirstName);
  }

  @Test
  void getSecondName() {
    // Assign
    String firstName = "testFirstName";
    String secondName = "testSecondName";
    User user = new User(firstName, secondName);

    // Act
    String actualSecondName = user.getSecondName();

    // Assert
    assertEquals(secondName, actualSecondName);
  }
}