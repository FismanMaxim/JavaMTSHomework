package org.example.CustomExceptions;

public class ProperSortingAlgorithmNotFoundException extends RuntimeException {
  public ProperSortingAlgorithmNotFoundException () {
    super();
  }
  public ProperSortingAlgorithmNotFoundException(String message) {
    super(message);
  }
}
