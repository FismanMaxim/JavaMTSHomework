package org.example;

import org.example.CustomExceptions.InvalidMessageForEnrichmentException;
import org.example.CustomExceptions.RequiredMessageEnricherNotFoundException;
import org.example.MessageEnrichers.MessageContentEnricher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrichmentService {
  private final List<MessageContentEnricher> enrichers;

  public EnrichmentService(List<MessageContentEnricher> enrichers) {
    this.enrichers = enrichers;
  }

  public Map<String, String> enrich (Message message) {
    Map<String, String> messageContent = new HashMap<>(message.getContent());
    Message.EnrichmentType targetEnrichmentType = message.getEnrichmentType();

    boolean requiredEnrichmentTypeFound = false;
    boolean enrichedSuccessfully = false;

    for (MessageContentEnricher enricher : enrichers) {
      if (enricher.getEnrichmentType() != targetEnrichmentType)
        continue;

      requiredEnrichmentTypeFound = true;
      try {
        enricher.enrich(messageContent);
        enrichedSuccessfully = true;
        break;
      } catch (InvalidMessageForEnrichmentException e) {
        System.out.println("Failed to enrich with " + enricher);
      }
    }

    if (!requiredEnrichmentTypeFound || !enrichedSuccessfully)
      throw new RequiredMessageEnricherNotFoundException();

    return messageContent;
  }
}
