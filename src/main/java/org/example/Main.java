package org.example;

import org.example.MessageEnrichers.MsisdnContentEnricher;
import org.example.UserStorage.UsersDatabase;
import org.example.UserStorage.UsersMsisdnContainer;

import java.util.List;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    EnrichmentService enrichmentService = new EnrichmentService(List.of(
            new MsisdnContentEnricher()
    ));

    UsersMsisdnContainer usersMsisdn = new UsersMsisdnContainer(Map.of(
            "123456789", new User("Jack", "Smith"),
            "987654321", new User("Emily", "Waffle")
    ));
    UsersDatabase.setMsisdnBase(usersMsisdn);

    Map<String, String> content = Map.of(
            "action", "button_click", "page", "book_card", "msisdn", "123456789"
    );
    Message message = new Message(content, Message.EnrichmentType.MSISDN);

    Map<String, String> newContent = enrichmentService.enrich(message);

    for (var key : newContent.keySet()) {
      System.out.println(key + ": " + newContent.get(key));
    }
  }
}