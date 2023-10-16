package org.example;

import org.example.CustomExceptions.ProperSortingAlgorithmNotFoundException;
import org.example.CustomExceptions.TooLargeListSizeException;
import org.example.SortingAlgorithms.BubbleSorter;
import org.example.SortingAlgorithms.JavaBuiltinSorter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ListSorterTest {
  @Test
  void constructorNullList() {
    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> new ListSorter(null));
  }

  @Test
  void constructorEmptyList() {
    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> new ListSorter(List.of()));
  }

  @Test
  void sortValidList() {
    // Assign
    ListSorter sorter = new ListSorter(List.of(
            new BubbleSorter(5),
            new JavaBuiltinSorter(20)
    ));
    List<Integer> list = List.of(4, 6, 2, 4, 7, -6, 0, -2, -4);
    ArrayList<Integer> expected = new ArrayList<>(list);
    Collections.sort(expected);

    // Act
    List<Integer> actual = sorter.sortList(list, SortingAlgorithmType.JavaBuiltin);

    // Assert
    Assertions.assertIterableEquals(expected, actual);
  }

  @Test
  void sortNullList() {
    // Assign
    ListSorter sorter = new ListSorter(List.of(
            new BubbleSorter(5),
            new JavaBuiltinSorter(20)
    ));
    List<Integer> list = null;

    // Act & Assert
    Assertions.assertThrows(NullPointerException.class, () -> sorter.sortList(list, SortingAlgorithmType.Bubble));
  }

  @Test
  void sortListWithAbsentAlgorithm() {
    // Assign
    ListSorter sorter = new ListSorter(List.of(
            new BubbleSorter(5)
    ));
    List<Integer> list = List.of(1, 2, 0);

    // Act & Assert
    Assertions.assertThrows(ProperSortingAlgorithmNotFoundException.class, () -> sorter.sortList(list, SortingAlgorithmType.JavaBuiltin));
  }

  @Test
  void noProperAlgorithmForSorting() {
    // Assign
    ListSorter sorter = new ListSorter(List.of(
            new JavaBuiltinSorter(10)
    ));
    List<Integer> list = List.of(4, 6, 2, 4, 7, -6, 0, -2, -4);

    // Act & Assign
    Assertions.assertThrows(ProperSortingAlgorithmNotFoundException.class, () -> sorter.sortList(list, SortingAlgorithmType.Bubble));
  }

  @Test
  void sortTooLargeList() {
    // Assign
    ListSorter sorter = new ListSorter(List.of(
            new BubbleSorter(5),
            new JavaBuiltinSorter(5)
    ));
    List<Integer> list = List.of(4, 6, 2, 4, 7, -6, 0, -2, -4);

    // Act & Assign
    Assertions.assertThrows(TooLargeListSizeException.class, () -> sorter.sortList(list, SortingAlgorithmType.Bubble));

  }
}