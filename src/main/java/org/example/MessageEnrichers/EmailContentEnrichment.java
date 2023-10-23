package org.example.MessageEnrichers;

import org.example.CustomExceptions.EmailKeyNotFoundException;
import org.example.CustomExceptions.InvalidMessageForEnrichmentException;
import org.example.Message;

import java.util.Map;

public class EmailContentEnrichment extends MessageContentEnricher {
  public static final String EMAIL_KEY = "email";
  public static final String EMAIL_SERVER_KEY = "emailServerKey";

  @Override
  public Message.EnrichmentType getEnrichmentType() {
    return Message.EnrichmentType.EMAIL;
  }

  @Override
  protected void verifyMessageContent(Map<String, String> messageContent) {
    super.verifyMessageContent(messageContent);
    if (!messageContent.containsKey(EMAIL_KEY))
      throw new EmailKeyNotFoundException();

    // An email must always contain exactly one symbol @
    if (countAtsInEmail(messageContent.get(EMAIL_KEY)) != 1)
      throw new InvalidMessageForEnrichmentException();
  }

  @Override
  protected void addEnrichment(Map<String, String> messageContent) {
    String email = messageContent.get(EMAIL_KEY);
    String emailServer = email.split("@")[1];

    messageContent.put(EMAIL_SERVER_KEY, emailServer);
  }

  private int countAtsInEmail(String email) {
    return email.length() - email.replace("@", "").length();
  }
}
