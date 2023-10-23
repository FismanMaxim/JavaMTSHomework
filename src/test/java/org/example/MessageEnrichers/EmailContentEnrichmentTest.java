package org.example.MessageEnrichers;

import org.example.Auxiliary.MapsAsserter;
import org.example.CustomExceptions.EmailKeyNotFoundException;
import org.example.CustomExceptions.InvalidMessageForEnrichmentException;
import org.example.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailContentEnrichmentTest {
  private static final String TEST_EMAIL = "test@gmail.com";

  @Test
  void getEnrichmentType() {
    // Assign
    EmailContentEnrichment enricher = new EmailContentEnrichment();

    // Act && Assert
    assertEquals(enricher.getEnrichmentType(), Message.EnrichmentType.EMAIL);
  }

  @Test
  void verifyValidMessageContent() {
    // Assign
    EmailContentEnrichment enricher = new EmailContentEnrichment();
    HashMap<String, String> messageContent = new HashMap<>();
    messageContent.put("testKey1", "testValue1");
    messageContent.put("testKey2", "testValue2");
    messageContent.put( EmailContentEnrichment.EMAIL_KEY, TEST_EMAIL);

    // Act & Assert
    Assertions.assertDoesNotThrow(() -> enricher.verifyMessageContent(messageContent));
  }

  @Test
  void failVerificationOnEmailWithoutAtSymbol() {
    // Assign
    EmailContentEnrichment enricher = new EmailContentEnrichment();
    HashMap<String, String> messageContent = new HashMap<>();
    messageContent.put( EmailContentEnrichment.EMAIL_KEY, "emailWithoutAtSymbol");

    // Act & Assert
    Assertions.assertThrowsExactly(InvalidMessageForEnrichmentException.class, () -> enricher.verifyMessageContent(messageContent));
  }

  @Test
  void shouldNotVerifyNullMessageContent() {
    // Assign
    EmailContentEnrichment enricher = new EmailContentEnrichment();
    Map<String, String> messageContent = null;

    // Act & Assert
    Assertions.assertThrows(InvalidMessageForEnrichmentException.class, () -> enricher.verifyMessageContent(messageContent));
  }

  @Test
  void shouldNotVerifyInvalidContentWithoutEmail() {
    // Assign
    EmailContentEnrichment enricher = new EmailContentEnrichment();
    Map<String, String> messageContent = new HashMap<>();
    messageContent.put("testKey1", "testValue1");
    messageContent.put("testKey2", "testValue2");

    // Act & Assert
    Assertions.assertThrowsExactly(EmailKeyNotFoundException.class, () -> enricher.verifyMessageContent(messageContent));
  }

  @Test
  void addEnrichmentValid() {
    // Assign
    EmailContentEnrichment enricher = new EmailContentEnrichment();
    Map<String, String> messageContent = new HashMap<>();
    messageContent.put(EmailContentEnrichment.EMAIL_KEY, TEST_EMAIL);
    messageContent.put("testKey1", "testValue1");
    messageContent.put("testKey2", "testValue2");
    Map<String, String> expected = new HashMap<>(messageContent);
    expected.put(EmailContentEnrichment.EMAIL_SERVER_KEY, TEST_EMAIL.split("@")[1]);

    // Act
    enricher.enrich(messageContent);

    // Assert
    MapsAsserter.assertEqual(expected, messageContent);
  }
}