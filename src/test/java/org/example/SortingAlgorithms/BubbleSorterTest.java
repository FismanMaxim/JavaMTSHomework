package org.example.SortingAlgorithms;

import org.example.CustomExceptions.TooLargeListSizeException;
import org.example.SortingAlgorithmType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSorterTest {
  @Test
  public void constructorInvalidListSize() {
    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BubbleSorter(-10));
  }

  @Test
  void returnBubbleAlgorithmType() {
    // Assign
    BubbleSorter sorter = new BubbleSorter(10);
    SortingAlgorithmType expected = SortingAlgorithmType.Bubble;

    // Act
    SortingAlgorithmType type = sorter.getAlgorithmType();

    // Assert
    assertEquals(expected, type);
  }

  @Test
  void SortValidList() {
    // Assign
    BubbleSorter sorter = new BubbleSorter(10);
    ArrayList<Integer> actual = new ArrayList<>(List.of(-3, -5, 6, 8, -2, 5));
    ArrayList<Integer> expected =new ArrayList<>(actual);
    Collections.sort(expected);

    // Act
    sorter.sort(actual);

    // Assert
    Assertions.assertIterableEquals(expected, actual);
  }

  @Test
  void SortNullList() {
    // Assign
    BubbleSorter sorter = new BubbleSorter(10);
    ArrayList<Integer> actual = null;

    // Act & Assign
    Assertions.assertThrows(NullPointerException.class, () -> sorter.sort(actual));
  }

  @Test
  void SortTooLargeList() {
    // Assign
    BubbleSorter sorter = new BubbleSorter(10);
    ArrayList<Integer> actual = new ArrayList<>(List.of(1, 3, 2, 4, 7, 3, 2, 5, 6, 8, 3, 2, 5, 6));

    // Act & Assign
    Assertions.assertThrows(TooLargeListSizeException.class, () -> sorter.sort(actual));
  }
}