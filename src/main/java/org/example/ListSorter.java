package org.example;

import org.example.CustomExceptions.ProperSortingAlgorithmNotFoundException;
import org.example.CustomExceptions.TooLargeListSizeException;
import org.example.SortingAlgorithms.SortingAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class ListSorter {
  private final List<SortingAlgorithm> _sortingAlgorithms;

  public ListSorter(List<SortingAlgorithm> algorithms) {
    if (algorithms == null) {
      throw new IllegalArgumentException();
    }

    _sortingAlgorithms = algorithms;
  }

  public List<Integer> sortList(List<Integer> originalList, SortingAlgorithmType algorithmType) {
    ArrayList<Integer> listCopy = copyList(originalList);

    boolean properAlgorithmFound = false;
    boolean sortedSuccessfully = false;

    for (SortingAlgorithm algorithm : _sortingAlgorithms) {
      if (algorithm.getAlgorithmType() != algorithmType)
        continue;

      properAlgorithmFound = true;

      try {
         algorithm.sort(listCopy);
         sortedSuccessfully = true;
         break;
      } catch (TooLargeListSizeException e) {
        System.out.println("Cannot sort list with " + algorithm + " because it is too large for this algorithm. Trying the next one...");
      }
    }

    if (properAlgorithmFound == false)
      throw new ProperSortingAlgorithmNotFoundException();
    else if (sortedSuccessfully == false)
      throw new TooLargeListSizeException();
    else
      return listCopy;
  }

  private ArrayList<Integer> copyList(List<Integer> list) {
    return new ArrayList<>(list);
  }
}
