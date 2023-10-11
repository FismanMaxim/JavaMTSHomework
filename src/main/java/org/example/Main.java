package org.example;

import org.example.SortingAlgorithms.BubbleSorter;
import org.example.SortingAlgorithms.JavaBuiltinSorter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
  public static void main(String[] args) {
    final int minListSize = 0;
    final int maxListSize = 100;
    final int minListValue = -1_000_000;
    final int maxListValue = 1_000_000;

    ListSorter sorter = new ListSorter(List.of(
            new BubbleSorter(10),
            new JavaBuiltinSorter(10),
            new BubbleSorter(20),
            new JavaBuiltinSorter(20),
            new BubbleSorter(maxListSize),
            new JavaBuiltinSorter(maxListSize)
    ));

    Random random = new Random();

    int listSize = random.nextInt(minListSize, maxListSize);
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < listSize; i++) {
      list.add(random.nextInt(minListValue,  maxListValue));
    }
    System.out.println("List before sorting");
    printList(list);

    List<Integer> javaBuiltinSortedList = sorter.sortList(list, SortingAlgorithmType.Bubble);
    List<Integer> bubbleSortedList = sorter.sortList(list, SortingAlgorithmType.JavaBuiltin);

    System.out.println();
    System.out.println("List sorted with bubble sorter:");
    printList(bubbleSortedList);
    System.out.println();
    System.out.println("List sorted with java builtin sorter:");
    printList(javaBuiltinSortedList);
  }

  private static void printList(List<Integer> list) {
    for (int i = 0; i < list.size(); i++) {
      System.out.print(list + " ");
    }
    System.out.println();
  }
}