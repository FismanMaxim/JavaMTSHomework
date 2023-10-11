package org.example.CustomExceptions;

public class TooLargeListSizeException extends RuntimeException {
  public TooLargeListSizeException() {
    super();
  }
  public TooLargeListSizeException(String message) {
    super(message);
  }
}
