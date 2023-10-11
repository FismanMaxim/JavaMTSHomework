package org.example;

import com.sun.jdi.AbsentInformationException;
import org.example.CustomExceptions.ProperSortingAlgorithmNotFoundException;
import org.example.CustomExceptions.TooLargeListSizeException;
import org.example.SortingAlgorithms.BubbleSorter;
import org.example.SortingAlgorithms.JavaBuiltinSorter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListSorterTest {
  @Test
  void Constructor_NullList() {
    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> new ListSorter(null));
  }

  @Test
  void SortList_ValidList_ReturnSortedList() {
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
  void SortList_NullList_ThrowNullArgumentException() {
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
  void SortList_AbsentAlgorithm_ThrowAbsentProperAlgorithmException() {
    // Assign
    ListSorter sorter = new ListSorter(List.of(
            new BubbleSorter(5)
    ));
    List<Integer> list = List.of(1, 2, 0);

    // Act & Assert
    Assertions.assertThrows(ProperSortingAlgorithmNotFoundException.class, () -> sorter.sortList(list, SortingAlgorithmType.JavaBuiltin));
  }

  @Test
  void SortList_NoProperAlgorithm_ThrowProperAlgorithmNotFoundException() {
    // Assign
    ListSorter sorter = new ListSorter(List.of(
            new JavaBuiltinSorter(10)
    ));
    List<Integer> list = List.of(4, 6, 2, 4, 7, -6, 0, -2, -4);

    // Act & Assign
    Assertions.assertThrows(ProperSortingAlgorithmNotFoundException.class, () -> sorter.sortList(list, SortingAlgorithmType.Bubble));
  }
}