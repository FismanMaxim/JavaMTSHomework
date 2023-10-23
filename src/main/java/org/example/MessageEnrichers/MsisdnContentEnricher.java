package org.example.MessageEnrichers;

import org.example.CustomExceptions.MsisdnKeyNotFoundException;
import org.example.Message;
import org.example.User;
import org.example.UserStorage.UsersDatabase;

import java.util.Map;

public class MsisdnContentEnricher extends MessageContentEnricher {
  public static final String MSISDN_KEY = "msisdn";
  public static final String FIRST_NAME_KEY = "firstName";
  public static final String SECOND_NAME_KEY = "secondName";

  @Override
  public Message.EnrichmentType getEnrichmentType() {
    return Message.EnrichmentType.MSISDN;
  }

  @Override
  protected void addEnrichment(Map<String, String> messageContent) {
    String msisdn = messageContent.get(MSISDN_KEY);
    User user = UsersDatabase.getMsisdnBase().findUserByMsisdn(msisdn);

    messageContent.put(FIRST_NAME_KEY, user.getFirstName());
    messageContent.put(SECOND_NAME_KEY, user.getSecondName());
  }

  @Override
  protected void verifyMessageContent(Map<String, String> messageContent) {
    super.verifyMessageContent(messageContent);
    if (!messageContent.containsKey(MSISDN_KEY))
      throw new MsisdnKeyNotFoundException();
  }
}
