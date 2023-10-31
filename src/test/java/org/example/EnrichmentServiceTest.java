package org.example;

import org.example.Auxiliary.MapsAsserter;
import org.example.CustomExceptions.RequiredMessageEnricherNotFoundException;
import org.example.MessageEnrichers.EmailContentEnrichment;
import org.example.MessageEnrichers.MessageContentEnricher;
import org.example.MessageEnrichers.MsisdnContentEnricher;
import org.example.UserStorage.UsersDatabase;
import org.example.UserStorage.UsersMsisdnContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class EnrichmentServiceTest {
  private static final String TEST_MSISDN = "1234567890";
  private static final String TEST_FIRST_NAME = "testUserFirstName";
  private static final String TEST_SECOND_NAME = "testUserSecondName";
  private static final User TEST_USER = new User(TEST_FIRST_NAME, TEST_SECOND_NAME);
  private static final String TEST_EMAIL = "test@gmail.com";

  @Test
  void enrichValidMessage() {
    // Assign
    List<MessageContentEnricher> enrichers = List.of(
            new MsisdnContentEnricher()
    );
    EnrichmentService service = new EnrichmentService(enrichers);
    HashMap<String, String> content = new HashMap<>();
    content.put("testKey1", "testValue1");
    content.put("testKey2", "testValue2");
    content.put(MsisdnContentEnricher.MSISDN_KEY, TEST_MSISDN);
    Message message = new Message(content, Message.EnrichmentType.MSISDN);
    UsersDatabase.setMsisdnBase(new UsersMsisdnContainer(Map.of(
            TEST_MSISDN, TEST_USER
    )));
    HashMap<String, String> expectedEnrichedContent = new HashMap<>(content);
    expectedEnrichedContent.put(MsisdnContentEnricher.FIRST_NAME_KEY, TEST_FIRST_NAME);
    expectedEnrichedContent.put(MsisdnContentEnricher.SECOND_NAME_KEY, TEST_SECOND_NAME);


    // Act
    Map<String, String> actualEnrichedContent = service.enrich(message);

    // Assert
    MapsAsserter.assertEqual(expectedEnrichedContent, actualEnrichedContent);
  }

  @Test
  void shouldThrowExceptionIfRequiredEnricherNotFound() {
    // Assign
    List<MessageContentEnricher> enrichers = List.of(
            new MsisdnContentEnricher()
    );
    EnrichmentService service = new EnrichmentService(enrichers);
    HashMap<String, String> content = new HashMap<>();
    content.put("testKey1", "testValue1");
    content.put(MsisdnContentEnricher.MSISDN_KEY, TEST_MSISDN);
    Message message = new Message(content, Message.EnrichmentType.EMAIL);
    UsersDatabase.setMsisdnBase(new UsersMsisdnContainer(Map.of(
            TEST_MSISDN, TEST_USER
    )));

    // Act
    Assertions.assertThrowsExactly(RequiredMessageEnricherNotFoundException.class, () -> service.enrich(message));
  }

  /**
   * Should enrich successfully a message that is valid for given enrichment type, but might be invalid for some others
   */
  @Test
  void enrichOnlyPartlyValidMessage() {
    // Assign
    List<MessageContentEnricher> enrichers = List.of(
            new MsisdnContentEnricher(),
            new EmailContentEnrichment()
    );
    EnrichmentService service = new EnrichmentService(enrichers);
    HashMap<String, String> content = new HashMap<>();
    content.put("testKey1", "testValue1");
    content.put("testKey2", "testValue2");
    content.put(EmailContentEnrichment.EMAIL_KEY, TEST_EMAIL);
    Message message = new Message(content, Message.EnrichmentType.EMAIL);
    HashMap<String, String> expectedEnrichedContent = new HashMap<>(content);
    expectedEnrichedContent.put(EmailContentEnrichment.EMAIL_SERVER_KEY, TEST_EMAIL.split("@")[1]);


    // Act
    Map<String, String> actualEnrichedContent = service.enrich(message);

    // Assert
    MapsAsserter.assertEqual(expectedEnrichedContent, actualEnrichedContent);
  }

  @Test
  void enrichMultipleMessagesConcurrently() {
    // region Assign
    HashMap<String, String> content = new HashMap<>();
    content.put("testKey1", "testValue1");
    content.put(EmailContentEnrichment.EMAIL_KEY, TEST_EMAIL);
    content.put(MsisdnContentEnricher.MSISDN_KEY,  TEST_MSISDN);

    List<Message> messages = List.of(
            new Message(content, Message.EnrichmentType.MSISDN),
            new Message(content, Message.EnrichmentType.MSISDN),
            new Message(content, Message.EnrichmentType.MSISDN),
            new Message(content, Message.EnrichmentType.EMAIL),
            new Message(content, Message.EnrichmentType.EMAIL),
            new Message(content, Message.EnrichmentType.EMAIL)
    );

    EnrichmentService service =  new EnrichmentService(List.of(
            new EmailContentEnrichment(),
            new MsisdnContentEnricher()
    ));

    UsersDatabase.setMsisdnBase(new UsersMsisdnContainer(Map.of(
            TEST_MSISDN, TEST_USER
    )));

    List<Map<String, String>> actualEnrichedMessagesContents = new CopyOnWriteArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(messages.size());

    // Calculate expected values
    var contentAfterMsisdnEnrichment = new HashMap<>(content);
    var contentAfterEmailEnrichment = new HashMap<>(content);
    contentAfterMsisdnEnrichment.put(MsisdnContentEnricher.FIRST_NAME_KEY, TEST_FIRST_NAME);
    contentAfterMsisdnEnrichment.put(MsisdnContentEnricher.SECOND_NAME_KEY, TEST_SECOND_NAME);
    contentAfterEmailEnrichment.put(EmailContentEnrichment.EMAIL_SERVER_KEY, TEST_EMAIL.split("@")[1]);
    List<Map<String, String>> expectedEnrichedMessagesContents = List.of(
            contentAfterMsisdnEnrichment,
            contentAfterMsisdnEnrichment,
            contentAfterMsisdnEnrichment,
            contentAfterEmailEnrichment,
            contentAfterEmailEnrichment,
            contentAfterEmailEnrichment
    );
    // endregion

    // region Act
    for (int i = 0; i < messages.size(); i++) {
      int finalI = i;
      executorService.submit(() -> {
        var enrichedMessageContent = service.enrich(messages.get(finalI));
        actualEnrichedMessagesContents.add(enrichedMessageContent);
        latch.countDown();
      });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      System.out.println("Latch was interrupted");
      Assertions.fail();
    }
    // endregion

    // region Assert
    boolean correct = true;
    if (actualEnrichedMessagesContents.size() != expectedEnrichedMessagesContents.size())
      correct = false;
    for (int i = 0; i < actualEnrichedMessagesContents.size() && correct; i++) {
      correct = MapsAsserter.compareEqual(actualEnrichedMessagesContents.get(i), expectedEnrichedMessagesContents.get(i));
    }
    Assertions.assertTrue(correct);
    // endregion
  }

  @Test
  void parallelEnrichmentOfSameMessage() {
    // region Assign
    HashMap<String, String> content = new HashMap<>();
    content.put("testKey1", "testValue1");
    content.put(MsisdnContentEnricher.MSISDN_KEY,  TEST_MSISDN);

    Message message = new Message(content, Message.EnrichmentType.MSISDN);

    EnrichmentService service =  new EnrichmentService(List.of(
            new MsisdnContentEnricher()
    ));

    UsersDatabase.setMsisdnBase(new UsersMsisdnContainer(Map.of(
            TEST_MSISDN, TEST_USER
    )));

    List<Map<String, String>> actualEnrichedMessagesContents = new CopyOnWriteArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(10);

    // Calculate expected values
    var expectedContentAfterEnrichment = content;
    expectedContentAfterEnrichment.put(MsisdnContentEnricher.FIRST_NAME_KEY, TEST_FIRST_NAME);
    expectedContentAfterEnrichment.put(MsisdnContentEnricher.SECOND_NAME_KEY, TEST_SECOND_NAME);
    // endregion

    // region Act
    for (int i = 0; i < 10; i++) {
      executorService.submit(() -> {
        var enrichedMessageContent = service.enrich(message);
        actualEnrichedMessagesContents.add(enrichedMessageContent);
        latch.countDown();
      });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      System.out.println("Latch was interrupted");
      Assertions.fail();
    }
    // endregion

    // region Assert
    for (var actualContent : actualEnrichedMessagesContents) {
      assert MapsAsserter.compareEqual(expectedContentAfterEnrichment, actualContent);
    }
    // endregion
  }
}