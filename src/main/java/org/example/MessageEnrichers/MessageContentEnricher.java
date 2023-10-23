package org.example.MessageEnrichers;

import org.example.CustomExceptions.InvalidMessageForEnrichmentException;
import org.example.Message;

import java.util.Map;

public abstract class MessageContentEnricher {
  public final void enrich(Map<String, String> messageContent) {
    verifyMessageContent(messageContent);

    addEnrichment(messageContent);
  }

  public abstract Message.EnrichmentType getEnrichmentType();

  protected void verifyMessageContent(Map<String, String> messageContent) {
    if (messageContent == null)
      throw new InvalidMessageForEnrichmentException();
  }

  protected abstract void addEnrichment(Map<String, String> messageContent);
}
