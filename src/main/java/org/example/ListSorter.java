package org.example;

import org.example.CustomExceptions.ProperSortingAlgorithmNotFoundException;
import org.example.CustomExceptions.TooLargeListSizeException;
import org.example.SortingAlgorithms.SortingAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class ListSorter {
  private final List<SortingAlgorithm> _sortingAlgorithms;

  public ListSorter(List<SortingAlgorithm> algorithms) {
    _sortingAlgorithms = algorithms;
  }

  public List<Integer> sortList(List<Integer> originalList, SortingAlgorithmType algorithmType) {
    ArrayList<Integer> listCopy = copyList(originalList);

    boolean properAlgorithmFound = false;

    for (SortingAlgorithm algorithm : _sortingAlgorithms) {
      if (algorithm.getAlgorithmType() != algorithmType)
        continue;

      properAlgorithmFound = true;

      try {
         algorithm.sort(listCopy);
         break;
      } catch (TooLargeListSizeException e) {
        System.out.println("Cannot sort list with " + algorithm + " because it is too large for this algorithm. Trying the next one...");
      }
    }

    if (properAlgorithmFound == false)
      throw new ProperSortingAlgorithmNotFoundException();
    else
      return listCopy;
  }

  private ArrayList<Integer> copyList(List<Integer> list) {
    return new ArrayList<>(list);
  }
}
