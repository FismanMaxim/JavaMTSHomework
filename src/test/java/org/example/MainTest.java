package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTest {
  @Test
  void successfulLaunch() {
    // Act && Assert
    Assertions.assertDoesNotThrow(() -> Main.main(null));
  }
}