package mts.homework.bookService.services;

public interface AuthorsRegistryServiceGatewayBase {
  boolean isAuthorWroteThisBook(String firstName, String lastName, String bookName);
}
