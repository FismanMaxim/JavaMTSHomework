package org.example.SortingAlgorithms;

import org.example.CustomExceptions.TooLargeListSizeException;
import org.example.SortingAlgorithmType;

import java.util.ArrayList;
import java.util.Collections;

public class JavaBuiltinSorter extends SortingAlgorithm {
  public JavaBuiltinSorter(int maxListSize) {
    super(maxListSize);
  }

  @Override
  public SortingAlgorithmType getAlgorithmType() {
    return SortingAlgorithmType.JavaBuiltin;
  }

  @Override
  public void sort(ArrayList<Integer> list) {
    if (list == null)
      throw new NullPointerException();
    if (list.size() > maxListSize)
      throw new TooLargeListSizeException();

    Collections.sort(list);
  }
}
