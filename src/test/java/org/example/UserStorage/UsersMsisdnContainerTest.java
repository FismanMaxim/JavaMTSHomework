package org.example.UserStorage;

import org.example.CustomExceptions.UserNotFoundException;
import org.example.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsersMsisdnContainerTest {
  @Test
  void correctUserAddGetHandling() {
    // Assign
    UsersMsisdnContainer container = new UsersMsisdnContainer();
    String msisdn1 = "1234567890";
    User user1 = new User("testName1", "testSurname1");
    String msisdn2 = "0987654321";
    User user2 = new User("testName2", "testSurname2");

    // Act
    container.updateUserByMsisdn(msisdn1, user1);
    container.updateUserByMsisdn(msisdn2, user2);

    // Assert
    assertEquals(container.findUserByMsisdn(msisdn2), user2);
  }

  @Test
  void throwUserNotFoundException() {
    // Assign
    UsersMsisdnContainer container = new UsersMsisdnContainer();
    String msisdn1 = "1234567890";
    User user1 = new User("testName1", "testSurname1");
    String msisdn2 = "0987654321";

    // Act
    container.updateUserByMsisdn(msisdn1, user1);

    // Assert
    Assertions.assertThrows(UserNotFoundException.class, () -> container.findUserByMsisdn(msisdn2));
  }
}