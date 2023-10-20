package org.example.SortingAlgorithms;

import org.example.CustomExceptions.TooLargeListSizeException;
import org.example.SortingAlgorithmType;

import java.util.ArrayList;
import java.util.List;

public class BubbleSorter extends SortingAlgorithm {
  public BubbleSorter(int maxListSize) {
    super(maxListSize);
  }

  @Override
  public SortingAlgorithmType getAlgorithmType() {
    return SortingAlgorithmType.Bubble;
  }

  @Override
  public void sort(ArrayList<Integer> list) {
    if (list == null)
      throw new NullPointerException();
    if (list.size() > maxListSize)
      throw new TooLargeListSizeException();

    for (int i = 0; i < list.size(); i++) {
      for (int j = i + 1; j < list.size(); j++) {
        if (list.get(i) > list.get(j)) {
          int tmp = list.get(i);
          list.set(i, list.get(j));
          list.set(j, tmp);
        }
      }
    }
  }
}
