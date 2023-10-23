package org.example.MessageEnrichers;

import org.example.Auxiliary.MapsAsserter;
import org.example.CustomExceptions.InvalidMessageForEnrichmentException;
import org.example.CustomExceptions.MsisdnKeyNotFoundException;
import org.example.Message;
import org.example.User;
import org.example.UserStorage.UsersDatabase;
import org.example.UserStorage.UsersMsisdnContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MsisdnContentEnricherTest {

  @Test
  void getEnrichmentType() {
    // Assign
    MsisdnContentEnricher enricher = new MsisdnContentEnricher();

    // Act && Assert
    assertEquals(enricher.getEnrichmentType(), Message.EnrichmentType.MSISDN);
  }

  @Test
  void verifyValidMessageContent() {
    // Assign
    MsisdnContentEnricher enricher = new MsisdnContentEnricher();
    HashMap<String, String> messageContent = new HashMap<>();
    String testMsisdn = "12345";
    messageContent.put(MsisdnContentEnricher.MSISDN_KEY, testMsisdn);
    messageContent.put("testKey1", "testValue1");
    messageContent.put("testKey2", "testValue2");

    // Act & Assert
    Assertions.assertDoesNotThrow(() -> enricher.verifyMessageContent(messageContent));
  }

  @Test
  void shouldNotVerifyNullMessageContent() {
    // Assign
    MsisdnContentEnricher enricher = new MsisdnContentEnricher();
    Map<String, String> messageContent = null;

    // Act & Assert
    Assertions.assertThrows(InvalidMessageForEnrichmentException.class, () -> enricher.verifyMessageContent(messageContent));
  }

  @Test
  void shouldNotVerifyInvalidContentWithoutMsisdn() {
    // Assign
    MsisdnContentEnricher enricher = new MsisdnContentEnricher();
    Map<String, String> messageContent = new HashMap<>();
    messageContent.put("testKey1", "testValue1");
    messageContent.put("testKey2", "testValue2");

    // Act & Assert
    Assertions.assertThrowsExactly(MsisdnKeyNotFoundException.class, () -> enricher.verifyMessageContent(messageContent));
  }

  @Test
  void addEnrichmentValid() {
    // Assign
    MsisdnContentEnricher enricher = new MsisdnContentEnricher();
    Map<String, String> messageContent = new HashMap<>();
    messageContent.put("msisdn", "12345");
    messageContent.put("testKey1", "testValue1");
    messageContent.put("testKey2", "testValue2");
    UsersDatabase.setMsisdnBase(new UsersMsisdnContainer(Map.of(
            "12345", new User("testName", "testSurname")
    )));
    Map<String, String> expected = new HashMap<>(messageContent);
    expected.put(MsisdnContentEnricher.FIRST_NAME_KEY, "testName");
    expected.put(MsisdnContentEnricher.SECOND_NAME_KEY, "testSurname");

    // Act
    enricher.enrich(messageContent);

    // Assert
    MapsAsserter.assertEqual(expected, messageContent);
  }
}