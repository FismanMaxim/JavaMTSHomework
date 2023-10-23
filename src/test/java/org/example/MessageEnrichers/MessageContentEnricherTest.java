package org.example.MessageEnrichers;

import org.example.User;
import org.example.UserStorage.UsersDatabase;
import org.example.UserStorage.UsersMsisdnContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class MessageContentEnricherTest {
  @Test
  void successfullyEnrichesValidMessage() {
    // Assign
    MessageContentEnricher enricher = new MsisdnContentEnricher();
    UsersDatabase.setMsisdnBase(new UsersMsisdnContainer(Map.of(
            "12345", new User("testName", "testSurname")
    )));
    Map<String, String> messageContent = new HashMap<>();
    messageContent.put("msisdn", "12345");
    messageContent.put("testKey1", "testValue1");
    messageContent.put("testKey2", "testValue2");

    // Act & Assert
    Assertions.assertDoesNotThrow(() -> enricher.enrich(messageContent));
  }
}