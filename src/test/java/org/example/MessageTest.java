package org.example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {

  @Test
  void getContent() {
    // Assign
    HashMap<String, String> content = new HashMap<>();
    content.put("testKey1", "testValue1");
    content.put("testKey2", "testValue2");
    Message message = new Message(content, Message.EnrichmentType.MSISDN);

    // Act
    var actual = message.getContent();

    // Assert
    assertEquals(content, actual);
  }

  @Test
  void getEnrichmentType() {
    // Assign
    HashMap<String, String> content = new HashMap<>();
    content.put("testKey1", "testValue1");
    content.put("testKey2", "testValue2");
    Message.EnrichmentType enrichmentType = Message.EnrichmentType.MSISDN;
    Message message = new Message(content, enrichmentType);

    // Act
    var actual = message.getEnrichmentType();

    // Assert
    assertEquals(enrichmentType, actual);
  }
}